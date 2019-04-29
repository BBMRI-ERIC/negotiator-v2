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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.docuverse.identicon.IdenticonUtil;

import com.nimbusds.jwt.JWTClaimsSet;
import de.samply.bbmri.auth.client.AuthClient;
import de.samply.bbmri.auth.client.InvalidKeyException;
import de.samply.bbmri.auth.client.InvalidTokenException;
import de.samply.bbmri.auth.client.jwt.JWTAccessToken;
import de.samply.bbmri.auth.client.jwt.JWTIDToken;
import de.samply.bbmri.auth.client.jwt.JWTRefreshToken;
import de.samply.bbmri.auth.rest.Scope;
import de.samply.bbmri.auth.utils.OAuth2ClientConfig;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.common.config.OAuth2Client;
import de.samply.string.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sessionscoped bean for all data of the session about the user.
 */
@ManagedBean
@SessionScoped
public class UserBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(UserBean.class);

	/**
	 * The subjects from the dummy data.
	 */
	public static final String DUMMY_DATA_SUBJECT_RESEARCHER = "https://auth-dev.mitro.dkfz.de/users/8";
	public static final String DUMMY_DATA_SUBJECT_BIOBANK_OWNER = "https://auth-dev.mitro.dkfz.de/users/7";

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
	private Biobank biobank = null;

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
     * The redirect URL used when the user lands at the negotiator from the directory, after making a new query. It contains the temporary query id.
     */
    private String newQueryRedirectURL;

	/**
	 * The collections of this user
	 */
	private List<Collection> collections;

    /**
     * The identity string of the user who sudo'd another
     */
    private String sudoMaster = null;

    private Boolean isAdmin = false;

	/**
	 * Basic Constructor for when the user bean is created without dependency injection.
	 */
	public UserBean() {
		init();
	}

    public String getSudoMaster() {
        return sudoMaster;
    }

    public void setSudoMaster(String sudoMaster) {
        this.sudoMaster = sudoMaster;
    }

    public Boolean sudoedUser() {
	    return sudoMaster!=null;
    }

    /**
	 * Executes a logout.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logout() throws IOException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

	    // a sudoed user logs out back to his original self
	    if(sudoedUser()) {
	        unsudoUser();
	        context.redirect("/admin/index.xhtml");
	        return;
        }

		OAuth2Client config = NegotiatorConfig.get().getOauth2();

		// invalidate session all session scoped beans are destroyed and a new
		// login won't steal old values
		context.invalidateSession();

        if(NegotiatorConfig.get().getNegotiator().isAuthenticationDisabled()) {
            /**
             * If authentication is disabled, redirect the user back to the root folder of this application
             */
            context.redirect(context.getRequestContextPath());
        } else {
            context.redirect(context.getRequestContextPath() + "/logout.xhtml");
        }
	}

	/**
	 * Inits the state.
	 */
	@PostConstruct
	public void init() {
		// create the session state
		state = new BigInteger(64, new SecureRandom()).toString(32);
	}

	/**
	 * Returns the URL for Perun
	 *
	 * @return the authentication url
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public String getAuthenticationUrl() throws UnsupportedEncodingException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) context.getRequest();
		return getAuthenticationUrl(req);
	}

    /**
     * Returns an auth URL that provides /index.xhtml as redirect url always
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getAuthenticationUrlIndex(HttpServletRequest request) throws UnsupportedEncodingException {

        // TODO: Add path, in case negotiator is not deployed in ROOT
        String requestURL = "/index.xhtml";

        return OAuth2ClientConfig.getRedirectUrl(NegotiatorConfig.get().getOauth2(), request.getScheme(),
                request.getServerName(), request.getServerPort(), request.getContextPath(),
                requestURL, state, Scope.OPENID, Scope.EMAIL, Scope.PROFILE, Scope.PHONE, Scope.GROUPNAMES);
    }


    /**
	 * Returns the URL for Perun
	 *
	 * @return the authentication url
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public String getAuthenticationRegisterUrl() throws UnsupportedEncodingException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) context.getRequest();
		return getAuthenticationRegisterUrl(req);
	}

	/**
	 * Returns the URL to Perun for the given HttpServletRequest. Uses the request query string to determine the
	 * redirect URL back to the negotiator.
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
     */
	public String getAuthenticationUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuilder requestURL = new StringBuilder(request.getServletPath());


		/**
		 * Construct a redirect URL to the authentication system and back.
		 */

		if (request.getQueryString() != null) {
			requestURL.setLength(0);
			requestURL.append("/index.xhtml");
		    setNewQueryRedirectURL(request.getServletPath() + "?" + request.getQueryString());
		}

		return OAuth2ClientConfig.getRedirectUrl(NegotiatorConfig.get().getOauth2(), request.getScheme(),
				request.getServerName(), request.getServerPort(), request.getContextPath(),
				requestURL.toString(), state, Scope.OPENID, Scope.EMAIL, Scope.PROFILE, Scope.PHONE, Scope.GROUPNAMES);
	}

    /**
     * Returns the URL to Perun for the given HttpServletRequest. Uses the request query string to determine the
     * redirect URL back to the negotiator.
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getAuthenticationRegisterUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder requestURL = new StringBuilder(request.getServletPath());

        // replace last entry to register
        requestURL.setLength(0);
        requestURL.append("/register.xhtml");

        /**
         * Construct a redirect URL to the authentication system and back.
         */

        if (request.getQueryString() != null) {
            requestURL.setLength(0);
            requestURL.append("/register.xhtml");
            setNewQueryRedirectURL(request.getServletPath() + "?" + request.getQueryString());
        }

        String returnURL = OAuth2ClientConfig.getRedirectUrlRegisterPerun(NegotiatorConfig.get().getOauth2(), request.getScheme(),
				request.getServerName(), request.getServerPort(), request.getContextPath(),
				requestURL.toString(), state, Scope.OPENID, Scope.EMAIL, Scope.PROFILE, Scope.PHONE, Scope.GROUPNAMES);

		return returnURL;
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

		logger.debug("AC Header: "+accessToken.getHeader());
		logger.debug("AC State : "+accessToken.getState());
		Map<String, Object> claims = accessToken.getClaimsSet().getClaims();

		for(String claim: claims.keySet()) {
			logger.debug("AC Claim "+claim+" : "+claims.get(claim));
		}

		/**
		 * Make sure that if the access token contains a state parameter, that it matches the state variable. If it does
		 * not, abort.
		 */
		if (!StringUtil.isEmpty(accessToken.getState()) && !state.equals(accessToken.getState())) {
			accessToken = null;
			refreshToken = null;
			biobankOwner = false;
			researcher = false;
			return;
		}

		userIdentity = client.getAccessToken().getSubject();
		userRealName = client.getUser().getName();
		userEmail = client.getUser().getEmail();

		createOrGetUser();
	}

	/**
	 * Fakes the given user identity. Works only in development mode.
	 * @param identity
     */
	public void fakeUser(String identity) {
		if(NegotiatorConfig.get().getNegotiator().isAuthenticationDisabled()) {
			this.userIdentity = identity;

			try(Config config = ConfigFactory.get()) {
				PersonRecord personRecord = config.dsl().selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(identity)).fetchOne();

				if(personRecord != null) {
					userRealName = personRecord.getAuthName();
					userEmail = personRecord.getAuthEmail();
					userId = personRecord.getId();
					createOrGetUser();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void unsudoUser() {
	    this.userIdentity = getSudoMaster();
	    setSudoMaster(null);
	    isAdmin = true;

        try(Config config = ConfigFactory.get()) {
            PersonRecord personRecord = config.dsl().selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(this.userIdentity)).fetchOne();

            if(personRecord != null) {
                userRealName = personRecord.getAuthName();
                userEmail = personRecord.getAuthEmail();
                userId = personRecord.getId();
                createOrGetUser();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sudos as another user. Only admin users can do that.
     * @param identity
     */
    public void sudoUser(String identity) {
        if(!isAdmin)
            return;

        setSudoMaster(this.userIdentity);
        isAdmin = false;
        this.userIdentity = identity;

        try(Config config = ConfigFactory.get()) {
            PersonRecord personRecord = config.dsl().selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(identity)).fetchOne();

            if(personRecord != null) {
                userRealName = personRecord.getAuthName();
                userEmail = personRecord.getAuthEmail();
                userId = personRecord.getId();
                createOrGetUser();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
				person.store();

				isAdmin = false;
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
				person.update();

				if(person.getIsAdmin())
					isAdmin = true;
			}

			loginValid = true;
            researcher = true;

			/**
			 * Check if the user is a biobanker
			 */
            collections = DbUtil.getCollections(config, person.getId());

            biobankOwner = collections.size() > 0;

            this.person = config.map(person, Person.class);

			userId = person.getId();

			config.get().commit();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true, if the user is an admin, false otherwise.
	 * @return
     */
	public Boolean isAdmin() {
	    return isAdmin;
//		List<String> admins = NegotiatorConfig.get().getNegotiator().getAdmins();
//
//		/**
//		 * Create the userBean if necessary
//		 */
//		if(getLoginValid() && admins != null && admins.contains(userIdentity)) {
//			return true;
//		} else {
//			return false;
//		}
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
		if (biobank == null)
			return null;
		return biobank.getId();
	}

	/**
	 * Gets the location name the user belongs to (only biobank owners)
	 *
	 * @return
	 */
	public String getLocationName() {
		if (biobank == null)
			return null;
		return biobank.getName();
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

	public Boolean hasNewQueryRedirectURL() {
		return newQueryRedirectURL != null;
	}

    public String getNewQueryRedirectURL() {
        return newQueryRedirectURL;
    }

    public void setNewQueryRedirectURL(String newQueryRedirectURL) {
        this.newQueryRedirectURL = newQueryRedirectURL;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
