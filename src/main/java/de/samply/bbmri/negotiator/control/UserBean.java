/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.control;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.docuverse.identicon.IdenticonUtil;

import de.samply.auth.client.AuthClient;
import de.samply.auth.client.InvalidKeyException;
import de.samply.auth.client.InvalidTokenException;
import de.samply.auth.client.jwt.JWTAccessToken;
import de.samply.auth.client.jwt.JWTIDToken;
import de.samply.auth.client.jwt.JWTRefreshToken;
import de.samply.auth.rest.RoleDTO;
import de.samply.auth.rest.Scope;
import de.samply.auth.utils.OAuth2ClientConfig;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.Constants;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.daos.PersonDao;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Location;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.LocationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.common.config.OAuth2Client;
import de.samply.string.util.StringUtil;

/**
 * Sessionscoped bean for all data of the session about the user.
 */
@ManagedBean
@SessionScoped
public class UserBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * TODO: Hardcoded location ID and name to set to on new users, if he is a biobank owner, as that info will come
	 * from Perun and/or Directory and yet not from Samply.AUTH. So for now we simply put new biobank owners into that
	 * location, and use our own created locationID as such.
	 */
	private static final int TEMP_LOCATION_ID_FOR_ALL_BIO_OWNERS = 1;
	private static final String TEMP_LOCATION_NAME_FOR_ALL_BIO_OWNERS = "Biobank Hamburg";

	/**
	 * The current userEmail (email). Null if the login is not valid
	 */
	private String userEmail = null;

	/**
	 * The current real name. Null if the login is not valid.
	 */
	private String userRealName = null;

	/**
	 * The current user identity (usually a URL that identifies the user). Null if the login is not valid.
	 */
	private String userIdentity = null;

	/**
	 * If the login is valid this value is true. False otherwise.
	 */
	private Boolean loginValid = false;

	/**
	 * If the user is a biobank owner or not.
	 */
	private Boolean biobankOwner = false;

	/**
	 * If the user is a researcher or not.
	 */
	private Boolean researcher = false;

	/**
	 * ID of the Location (only biobank owners)
	 */
	private Location location = null;

	/**
	 * The *mapped* user ID in the database.
	 */
	private int userId = 0;

	/** The JWTs from OAuth2. */
	private JWTAccessToken accessToken;

	/** The JWT Open ID token. */
	private JWTIDToken idToken;

	/** The JWT refresh token. */
	private JWTRefreshToken refreshToken;

	/**
	 * The state of this session. This is a random string for OAuth2 used to prevent cross site forgery attacks.
	 */
	private String state;

	/**
	 * The Person from the database.
	 */
	private Person person;

	/**
	 * Executes a logout.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logout() throws IOException {
		OAuth2Client config = NegotiatorConfig.get().getOauth2();
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

		// invalidate session all session scoped beans are destroyed and a new
		// login won't steal old values
		context.invalidateSession();

		// redirect user away
		context.redirect(OAuth2ClientConfig.getLogoutUrl(config, context.getRequestScheme(),
		        context.getRequestServerName(), context.getRequestServerPort(), context.getRequestContextPath(), "/"));
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		// create the session state
		state = new BigInteger(64, new SecureRandom()).toString(32);
	}

	/**
	 * Returns the URL for Samply.Auth
	 *
	 * @return the authentication url
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public String getAuthenticationUrl() throws UnsupportedEncodingException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) context.getRequest();

		StringBuffer requestURL = new StringBuffer(context.getRequestServletPath());
		if (req.getQueryString() != null) {
			requestURL.append("?").append(req.getQueryString());
		}

		return OAuth2ClientConfig.getRedirectUrl(NegotiatorConfig.get().getOauth2(), context.getRequestScheme(),
		        context.getRequestServerName(), context.getRequestServerPort(), context.getRequestContextPath(),
		        requestURL.toString(), null, state, Scope.OPENID);
	}

	/**
	 * Lets the user login and sets all necessary fields.
	 *
	 * @param client
	 *            the client
	 * @throws InvalidTokenException
	 *             the invalid token exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 */
	public void login(AuthClient client) throws InvalidTokenException, InvalidKeyException {
		accessToken = client.getAccessToken();
		idToken = client.getIDToken();
		refreshToken = client.getRefreshToken();

		/**
		 * Make sure that if the access token contains a state parameter, that it matches the state variable. If it does
		 * not, abort.
		 */
		if (!StringUtil.isEmpty(accessToken.getState()) && !state.equals(accessToken.getState())) {
			accessToken = null;
			idToken = null;
			refreshToken = null;
			biobankOwner = false;
			researcher = false;
			return;
		}

		loginValid = true;
		userIdentity = idToken.getSubject();
		userRealName = idToken.getName();
		userEmail = idToken.getEmail();

		/**
		 * Check all roles. If the user is a biobank owner, set biobankOwner to true.
		 */
		List<RoleDTO> roles = client.getIDToken().getRoles();

		biobankOwner = false;
		researcher = false;

		for (RoleDTO role : roles) {
			if (Constants.OWNER_ROLE.equalsIgnoreCase(role.getIdentifier())) {
				biobankOwner = true;
			}

			if (Constants.RESEARCHER_ROLE.equalsIgnoreCase(role.getIdentifier())) {
				researcher = true;
			}
		}

		createOrGetUser();
	}

	/**
	 * Creates a location
	 *
	 * @param locationId
	 * @param locationName
	 * @return Location
	 */
	private Location createLocation(Integer locationId, String locationName) {
		Location location = null;

		try (Config config = ConfigFactory.get()) {
			LocationRecord locationRecord = config.dsl().newRecord(Tables.LOCATION);
			locationRecord.setId(locationId);
			locationRecord.setName(locationName);
			locationRecord.store();

			location = new Location();
			location.setId(locationId);
			location.setName(locationName);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Gets the location for a location ID
	 *
	 * @param locationId
	 * @return Location
	 */
	private Location getLocation(Integer locationId) {
		Location location = null;

		try (Config config = ConfigFactory.get()) {
			LocationRecord locationRecord = config.dsl().selectFrom(Tables.LOCATION)
			        .where(Tables.LOCATION.ID.eq(locationId)).fetchOne();

			if (locationRecord == null) {
				return null;
			}

			location = new Location();
			location.setId(locationRecord.getId());
			location.setName(locationRecord.getName());
			return location;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * If the "userIdentity" does not exist in the database, create it.
	 */
	private void createOrGetUser() {
		try (Config config = ConfigFactory.get()) {

			PersonRecord person = config.dsl().selectFrom(Tables.PERSON)
			        .where(Tables.PERSON.AUTH_SUBJECT.eq(userIdentity)).fetchOne();

			/**
			 * If the user hasn't been to this negotiator before, store him in the database
			 */
			if (person == null) {
				person = config.dsl().newRecord(Tables.PERSON);
				person.setAuthName(userRealName);
				person.setAuthEmail(userEmail);
				person.setAuthSubject(userIdentity);

				/**
				 * Set the image as identicon. As long as the identity provider does not support avatars, generate an
				 * identicon by default.
				 */
				person.setPersonImage(IdenticonUtil.generateIdenticon(256));

				if (biobankOwner) {

					// TODO: Update this to Perun and/or Directory given
					// Location data
					location = getLocation(TEMP_LOCATION_ID_FOR_ALL_BIO_OWNERS);
					if (location == null) {
						location = createLocation(TEMP_LOCATION_ID_FOR_ALL_BIO_OWNERS,
						        TEMP_LOCATION_NAME_FOR_ALL_BIO_OWNERS);
					}

					person.setLocationId(location.getId());
				}
				person.store();
			} else {
				/**
				 * Otherwise just update some fields.
				 */
				person.setAuthName(userRealName);
				person.setAuthEmail(userEmail);

				/**
				 * Set the identicon, if the identity provider does not support avatars. And only if it is still null
				 */
				if (person.getPersonImage() == null) {
					person.setPersonImage(IdenticonUtil.generateIdenticon(256));
				}

				if (biobankOwner) {
					location = getLocation(person.getLocationId());

					// TODO: Update this to Perun and/or Directory given
					// Location data
					if (location == null) {
						location = createLocation(TEMP_LOCATION_ID_FOR_ALL_BIO_OWNERS,
						        TEMP_LOCATION_NAME_FOR_ALL_BIO_OWNERS);
					}
				}
				person.update();
			}

			PersonDao dao = new PersonDao();
			this.person = dao.mapper().map(person);

			userId = person.getId();

			config.get().commit();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Profile.
	 *
	 * @return the string
	 */
	public String profile() {
		return null;
	}

	/**
	 * Gets the userEmail.
	 *
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * Sets the userEmail.
	 *
	 * @param userEmail
	 *            the new userEmail
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * Gets the real name.
	 *
	 * @return the real name
	 */
	public String getUserRealName() {
		return userRealName;
	}

	/**
	 * Sets the real name.
	 *
	 * @param userRealName
	 *            the new real name
	 */
	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	/**
	 * Gets the user identity.
	 *
	 * @return the user identity
	 */
	public String getUserIdentity() {
		return userIdentity;
	}

	/**
	 * Sets the user identity.
	 *
	 * @param userIdentity
	 *            the new user identity
	 */
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId
	 *            the new user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Gets the access token.
	 *
	 * @return the access token
	 */
	public JWTAccessToken getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the access token.
	 *
	 * @param accessToken
	 *            the new access token
	 */
	public void setAccessToken(JWTAccessToken accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Gets the id token.
	 *
	 * @return the id token
	 */
	public JWTIDToken getIdToken() {
		return idToken;
	}

	/**
	 * Sets the id token.
	 *
	 * @param idToken
	 *            the new id token
	 */
	public void setIdToken(JWTIDToken idToken) {
		this.idToken = idToken;
	}

	/**
	 * Gets the refresh token.
	 *
	 * @return the refresh token
	 */
	public JWTRefreshToken getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Sets the refresh token.
	 *
	 * @param refreshToken
	 *            the new refresh token
	 */
	public void setRefreshToken(JWTRefreshToken refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state
	 *            the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the login valid.
	 *
	 * @return the login valid
	 */
	public Boolean getLoginValid() {
		return loginValid;
	}

	/**
	 * Sets the login valid.
	 *
	 * @param loginValid
	 *            the new login valid
	 */
	public void setLoginValid(Boolean loginValid) {
		this.loginValid = loginValid;
	}

	/**
	 * Gets the biobank owner.
	 *
	 * @return the biobank owner
	 */
	public Boolean getBiobankOwner() {
		return biobankOwner;
	}

	/**
	 * Sets the biobank owner.
	 *
	 * @param biobankOwner
	 *            the new biobank owner
	 */
	public void setBiobankOwner(Boolean biobankOwner) {
		this.biobankOwner = biobankOwner;
	}

	/**
	 * Gets the location ID the user belongs to (only biobank owners)
	 *
	 * @return
	 */
	public Integer getLocationId() {
		if (location == null)
			return null;
		return location.getId();
	}

	/**
	 * Gets the location name the user belongs to (only biobank owners)
	 *
	 * @return
	 */
	public String getLocationName() {
		if (location == null)
			return null;
		return location.getName();
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Boolean getResearcher() {
		return researcher;
	}

	public void setResearcher(Boolean researcher) {
		this.researcher = researcher;
	}
}
