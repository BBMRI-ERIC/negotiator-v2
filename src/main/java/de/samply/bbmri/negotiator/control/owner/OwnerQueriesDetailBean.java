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

package de.samply.bbmri.negotiator.control.owner;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.io.MemoryUsageSetting;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.CollectionLifeCycleStatus;
import de.samply.bbmri.negotiator.util.DataCache;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import org.jooq.Record;
import org.jooq.Result;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class OwnerQueriesDetailBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(OwnerQueriesDetailBean.class);
	private String LOGGING_PREFIX = "97776fdc5f92-OwnerQueriesDetailBean ";

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

	@ManagedProperty(value = "#{fileUploadBean}")
	private FileUploadBean fileUploadBean;

	/**
	 * The structured query object
	 */
	private String humanReadableQuery = null;

	/**
     * The OwnerQueryStatsDTO object used to get all the information for queries.
     */
	private List<OwnerQueryStatsDTO> queries;

	/**
	 * The currently active flag filter. Set this to whatever flag you want and you will see the flagged queries only.
	 */
	private Flag flagFilter = Flag.UNFLAGGED;
	private Boolean isTestRequest = false;

	/**
     * The id of the query selected from owner.index.xhtml page, if there is one
     */
	private int queryId;

	/**
     * The selected query, if there is one
     */
	private Query selectedQuery = null;

	 /**
     * The input text box for the user to make a comment.
     */
    private String commentText;

	/**
     * The list of comments for the selected query
     */
    private List<CommentPersonDTO> comments;

    /**
     * The list of biobanks this owner is associated with
     */
    private List<BiobankRecord> associatedBiobanks;

	private final HashMap<Integer, String> biobankNames = null;

	/**
     * The list of offers(private comments) for the selected query
     */
    private List<OfferPersonDTO> offerPersonDTO;

	/**
	 * A boolean that specifies if a non-confidential view must be rendered for a non-confidential biobanker(for connector) and vise versa
	 */
	private boolean nonConfidential = true;

	/**
	 * The list of offerPersonDTO's, hence it's a list of lists.
	 */
	private List<List<OfferPersonDTO>> listOfSampleOffers = new ArrayList<>();

	private final DataCache dataCache = DataCache.getInstance();

	private int commentCount;
	private int unreadCommentCount = 0;
	private int privateNegotiationCount;
	private int unreadPrivateNegotiationCount = 0;

	private List<Person> personList;

	private final HashMap<String, List<CollectionLifeCycleStatus>> sortedCollections = new HashMap<>();

	/**
	 * Lifecycle Collection Data (Form, Structure)
	 */
	private RequestLifeCycleStatus requestLifeCycleStatus = null;
	private Integer collectionId;
	private Integer biobankId;
	private String nextCollectionLifecycleStatusStatus;
	private Integer numberOfSamplesAvailable;
	private Integer numberOfPatientsAvailable;
	private String indicateAccessConditions;
	private String shippedNumber;
	private Part  mtaFile;
	private Part dtaFile;
	private Part otherAccessFile;

	private HashMap<String, String> locatorRedirectUrls = new HashMap<>();
	private HashMap<String, String> locatorRedirectUrlsDisplayList = new HashMap<>();

	private static final int DEFAULT_BUFFER_SIZE = 10240;

	private final FileUtil fileUtil = new FileUtil();
	private List<FacesMessage> fileValidationMessages = new ArrayList<>();
	Negotiator negotiator = NegotiatorConfig.get().getNegotiator();

    /**
     * initialises the page by getting all the comments for a selected(clicked on) query
     */
	public String initialize() {
        setNonConfidential(false);

        try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId, userBean.getUserId()));

			/**
			 * Get the selected(clicked on) query from the list of queries for the owner
			 */
			for(OwnerQueryStatsDTO ownerQueryStatsDTO : getQueries()) {
				if(ownerQueryStatsDTO.getQuery().getId() == queryId) {
					selectedQuery = ownerQueryStatsDTO.getQuery();
					setCommentCountAndUreadCommentCount(ownerQueryStatsDTO);
				}
			}

			if(selectedQuery != null) {
				try {
					JSONParser parser = new JSONParser();
					JSONObject jsonObjectOriginalRequest = (JSONObject) parser.parse(selectedQuery.getJsonText());
					JSONArray searchQueriesJson = (JSONArray)jsonObjectOriginalRequest.get("searchQueries");
					for(Object queryJson : searchQueriesJson) {
						JSONObject queryJsonObject = (JSONObject)queryJson;
						JSONArray collections = (JSONArray)queryJsonObject.get("collections");
						for(Object collectionObject : collections) {
							JSONObject collection = (JSONObject) collectionObject;
							Object locatorBacklinkUrl = collection.get("redirectUrl");
							if(locatorBacklinkUrl != null && !locatorBacklinkUrl.toString().isEmpty()) {
								locatorRedirectUrls.put((String) collection.get("biobankId"), locatorBacklinkUrl.toString());
							}
						}
					}
				} catch (ParseException e) {
					logger.error(LOGGING_PREFIX + "ERROR-NG-0000114: Parsing json for query: " + queryId + " - " + selectedQuery.getJsonText());
					e.printStackTrace();
				}
			}

			associatedBiobanks = DbUtil.getAssociatedBiobanks(config, queryId, userBean.getUserId());

			for (int i = 0; i < associatedBiobanks.size(); ++i) {
				BiobankRecord biobank = associatedBiobanks.get(i);
				listOfSampleOffers.add(DbUtil.getOffers(config, queryId, biobank.getId(), userBean.getUserId()));
				if(locatorRedirectUrls.containsKey(biobank.getDirectoryId())) {
					locatorRedirectUrlsDisplayList.put(biobank.getName(), locatorRedirectUrls.get(biobank.getDirectoryId()));
				}
			}



            if(selectedQuery != null) {
				RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
				ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
				QueryDTO queryDTO = mapper.readValue(selectedQuery.getJsonText(), QueryDTO.class);
				setHumanReadableQuery(queryDTO.getHumanReadable());
			} else {

            	/*
            	 * Check why the selected query is null. There could be two possibilities.
            	 */
                Query query = DbUtil.checkIfQueryExists(config, queryId);
            	if(query == null){

					/**
					 * If 'query' is null, it means that the 'selected query' simply does not exist in our system.
					 */
					return "/errors/not-found.xhtml";

				}else{

            		/*
            		 *If 'query' is not null this means the biobanker does not have access privileges to the 'selected query'.
            		 * We give a partial read access to him now.
            		 */
            		logger.info("Giving temporary read rights to confidential biobanker");
					setNonConfidential(true);
					setSelectedQuery(query);

					// also add it to the list of queries loaded, so it appears on the navigation in the left for
                    // convienience
                    OwnerQueryStatsDTO addme = new OwnerQueryStatsDTO();
                    addme.setQuery(query);
                    Person queryAuthor = DbUtil.getPersonDetails(config, query.getResearcherId());
                    addme.setQueryAuthor(queryAuthor);
					queries.add(0, addme);
					return null;
				}
            }

			setPersonListForRequest(config, selectedQuery.getId());

            /*
             * Initialize Lifecycle Status
             */
			requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
			requestLifeCycleStatus.initialise();
			requestLifeCycleStatus.initialiseCollectionStatus();

			for(BiobankRecord biobankRecord : associatedBiobanks) {
				createCollectionListSortedByStatus(biobankRecord.getId());
			}

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return null;
	}

	private void setPersonListForRequest(Config config, Integer queryId) {
		personList = DbUtil.getPersonsContactsForRequest(config, queryId);
	}

	private void createCollectionListSortedByStatus(Integer biobankIds) {
		for(CollectionLifeCycleStatus collectionLifeCycleStatus : requestLifeCycleStatus.getCollectionsForBiobank(biobankIds)) {
			if(collectionLifeCycleStatus.getStatus() == null) {
				if(!sortedCollections.containsKey("ERRORState")) {
					sortedCollections.put("ERRORState", new ArrayList<>());
				}
				sortedCollections.get("ERRORState").add(collectionLifeCycleStatus);
			} else {
				if(!sortedCollections.containsKey(collectionLifeCycleStatus.getStatus().getStatus())) {
					sortedCollections.put(collectionLifeCycleStatus.getStatus().getStatus(), new ArrayList<>());
				}
				sortedCollections.get(collectionLifeCycleStatus.getStatus().getStatus()).add(collectionLifeCycleStatus);
			}
		}
	}

	private String replaceNotNull(String htmlTemplate, String replace, String replaceWith) {
		if(replaceWith != null) {
			return htmlTemplate.replaceAll(replace, replaceWith);
		} else {
			return htmlTemplate.replaceAll(replace, "-");
		}
	}

	private String replaceTemplate(String htmlTemplate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		Person researcher = getUserDataForResearcher(selectedQuery.getResearcherId());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__RequestTitle", selectedQuery.getTitle());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Date", dateFormat.format(selectedQuery.getQueryCreationTime()));
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__ID", selectedQuery.getId().toString());

		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Researcher", researcher.getAuthName());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Email", researcher.getAuthEmail());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Organisation", researcher.getOrganization());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Description", selectedQuery.getRequestDescription());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Projectdescription", selectedQuery.getText());
		htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Ethics", selectedQuery.getEthicsVote());

		return htmlTemplate;
	}

	public boolean displayLocatorUrl() {
		if(locatorRedirectUrlsDisplayList.isEmpty()) {
			return false;
		}
		return true;
	}

	public HashMap<String, String> getLocatorURLList() {
		return locatorRedirectUrlsDisplayList;
	}

	public void getRequestPDF() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		// Build pdf of request
		String tempPdfOutputFilePath = "/tmp/" + UUID.randomUUID().toString() + ".pdf";
		try {
			File inputHTML = new File(getClass().getClassLoader().getResource("pdfTemplate").getPath(), "RequestTemplate.html");
			byte[] encoded = Files.readAllBytes(Paths.get(inputHTML.getAbsolutePath()));
			String htmlTemplate = new String(encoded, "UTF-8");
			htmlTemplate = replaceTemplate(htmlTemplate);

			Document document = Jsoup.parse(htmlTemplate);
			document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
			org.w3c.dom.Document doc = new W3CDom().fromJsoup(document);

			String baseUri = FileSystems.getDefault()
					.getPath("D:")
					.toUri()
					.toString();
			OutputStream os = new FileOutputStream(tempPdfOutputFilePath);
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.toStream(os);
			builder.withW3cDocument(doc, baseUri);
			builder.run();
			os.close();
		} catch (IOException e) {
			System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000096: Problem creating pdf for request, query: " + queryId);
			e.printStackTrace();
		}


		// Merge uploaded pdf attachments of the query
		try(Config config = ConfigFactory.get()) {
			List<QueryAttachmentDTO> attachments = DbUtil.getQueryAttachmentRecords(config, queryId);
			PDFMergerUtility PDFmerger = new PDFMergerUtility();
			PDFmerger.setDestinationFileName(tempPdfOutputFilePath);
			File file = new File(tempPdfOutputFilePath);
			PDFmerger.addSource(file);
			for(QueryAttachmentDTO attachment : attachments) {
				File file_attachment = extracted(attachment);
				if(file_attachment != null) {
					PDFmerger.addSource(file_attachment);
				}
			}
			PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		} catch (Exception e) {
			System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000095: Problem getting and Merging query attachments for query: " + queryId);
			e.printStackTrace();
		}

		// return pdf file to download
		File file = new File(tempPdfOutputFilePath);
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment;filename=\""+ file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} catch (Exception e) {
			System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000097: Problem creating pdf for download, query: " + queryId);
			e.printStackTrace();
		} finally {
			input.close();
			output.close();
		}
		context.responseComplete();
	}

	private File extracted(QueryAttachmentDTO attachment) {
		if(attachment.getAttachment().endsWith(".pdf")) {
			String filename = fileUtil.getStorageFileName(queryId, attachment.getId(), ".pdf");
			return new File(negotiator.getAttachmentPath(), filename);
		}
		if(attachment.getAttachment().endsWith(".docx")) {
			String filename = fileUtil.getStorageFileName(queryId, attachment.getId(), ".docx");
			return new File(negotiator.getAttachmentPath(), filename + ".pdf");
		}
		return null;
	}

	/**
     * Leave query as a bio bank owner.
     *
     * @param queryDto
     * @return
     */
    public void ignoreQuery(OwnerQueryStatsDTO queryDto) {
        try (Config config = ConfigFactory.get()) {
			config.commit();
            queries = null;
            flagQuery(queryDto, Flag.IGNORED);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mark query as starred
     * @param queryDto
     */
    public void starQuery(OwnerQueryStatsDTO queryDto){
        flagQuery(queryDto, Flag.STARRED);
    }

    /**
     * Mark query as archived
     * @param queryDto
     */
    public void archiveQuery(OwnerQueryStatsDTO queryDto){
        flagQuery(queryDto, Flag.ARCHIVED);
    }

    /**
     * Mark the given query with the given flag.
     * @param queryDto
     * @param flag
     */
    private void flagQuery(OwnerQueryStatsDTO queryDto, Flag flag) {
        try (Config config = ConfigFactory.get()) {
			DbUtil.flagQuery(config, queryDto, flag, userBean.getUserId());
            config.get().commit();
            queries = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	private void setCommentCountAndUreadCommentCount(QueryStatsDTO query) {
		commentCount = query.getCommentCount();
		unreadCommentCount = query.getUnreadCommentCount();
		privateNegotiationCount = query.getPrivateNegotiationCount();
		unreadPrivateNegotiationCount = query.getUnreadPrivateNegotiationCount();
	}

	/**
     * Sorts the queries such that the archived ones appear at the end.
     *
     * @return
     */
    public void sortQueries(){
        if (queries == null || queries.isEmpty()) {
            return;
        } else {
            Collections.sort(queries, new Comparator<OwnerQueryStatsDTO>() {
                @Override
                public int compare(OwnerQueryStatsDTO obj1, OwnerQueryStatsDTO obj2) {
                    if(obj1.isArchived() && obj2.isArchived()) {
                        return 0;
                    } else if(obj1.isArchived()) {
                        return 1;
                    } else if(obj2.isArchived()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

        }
    }

	/**
	 * Add search filter
	 */
	public void addFilter() {
		queries = null;
		sessionBean.addFilter();
	}

	/**
	 * Removes the search filter.
	 *
	 * @param arg
	 *
	 */
	public void removeFilter(String arg) {
		queries = null;
		sessionBean.removeFilter(arg);
	}

	/**
	 * Split search terms by list of delimiters
	 * @return unique search terms
	 */
	public Set<String> getFilterTerms() {
		Set<String> filterTerms = new HashSet<>();
		for(String filters : sessionBean.getFilters()) {
			// split by 0 or more spaces, followed by either 'and','or', comma or more spaces
			String[] filterTermsArray = filters.split("\\s*(and|or|,)\\s*");
			Collections.addAll(filterTerms, filterTermsArray);
		}
		return filterTerms;
	}

	/**
	 * Returns the list of queries in which the current bio bank owner is a part of(all queries that on owner can see)
	 *
	 * @return queries
	 */
	public List<OwnerQueryStatsDTO> getQueries() {
		if (queries == null) {
			try (Config config = ConfigFactory.get()) {
				queries = DbUtil.getOwnerQueries(config, userBean.getUserId(), getFilterTerms(), flagFilter, isTestRequest);

				for (int i = 0; i < queries.size(); ++i) {
					getPrivateNegotiationCountAndTime(i);
				}

				sortQueries();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	public void getPrivateNegotiationCountAndTime(int index){
		try(Config config = ConfigFactory.get()) {
			Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForBiobanker(config, queries.get(index).getQuery().getId(), userBean.getUserId());
			queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
			queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
			int collection_counts = result.get(0).getValue("number_of_collections", Integer.class);
			if(collection_counts <= 0) {
				collection_counts = 1;
			}
			int unread_private_negotiation_count = result.get(0).getValue("unread_private_negotiation_count", Integer.class) / collection_counts;
			queries.get(index).setUnreadPrivateNegotiationCount(unread_private_negotiation_count);
		} catch (SQLException e) {
			System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
			e.printStackTrace();
		}
	}

	/*
	 * Lifecycle Collection update
	 */
	public String updateCollectionLifecycleStatus() {
		if(biobankId != 0) {
			return updateCollectionLifecycleStatusByBiobank(biobankId);
		} else {
			return updateCollectionLifecycleStatus(collectionId);
		}
	}

	public String updateCollectionLifecycleStatus(Integer collectionId) {
		if(nextCollectionLifecycleStatusStatus == null || nextCollectionLifecycleStatusStatus.length() == 0) {
			return "";
		}
		String status = nextCollectionLifecycleStatusStatus.split("\\.")[1];
		String statusType = nextCollectionLifecycleStatusStatus.split("\\.")[0];
		if(statusType.equalsIgnoreCase("notselected")) {
			return "";
		}

		requestLifeCycleStatus.nextStatus(status, statusType, createStatusJson(status), userBean.getUserId(), collectionId);
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	public String updateCollectionLifecycleStatusByBiobank(Integer biobankId) {
		List<CollectionLifeCycleStatus> collectionList = requestLifeCycleStatus.getCollectionsForBiobank(biobankId);
		for(CollectionLifeCycleStatus collectionLifeCycleStatus : collectionList) {
			updateCollectionLifecycleStatus(collectionLifeCycleStatus.getCollectionId());
		}
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	private String createStatusJson(String status) {
		if(status.equals(LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE)) {
			String result = "{\"numberAvaiableSamples\":\"";
			if(numberOfSamplesAvailable != null ) {
				result += numberOfSamplesAvailable;
			} else {
				result += 0;
			}
			result += "\", \"numberAvaiablePatients\":\"";
			if(numberOfPatientsAvailable != null ) {
				result += numberOfPatientsAvailable;
			} else {
				result += 0;
			}
			result += "\"}";
			return result;
		}
		if(status.equals(LifeCycleRequestStatusStatus.INDICATE_ACCESS_CONDITIONS)) {
			String result = "{";
			String seperatorForJason = "";
			if (indicateAccessConditions != null && indicateAccessConditions.length() > 0) {
				result += "\"indicateAccessConditions\":\"" + indicateAccessConditions + "\"";
				seperatorForJason = ",";
			}
			result += seperatorForJason + storeFilesForAccessCondition() + "}";
			return result;
		}
		if(shippedNumber != null && shippedNumber.length() > 0) {
			return "{\"shippedNumber\":\"" + shippedNumber + "\"}";
		}
		return null;
	}

	private String storeFilesForAccessCondition() {
		StringBuilder result = new StringBuilder();
		result.append("\"indicateAccessConditionFiles\":[");
		String seperatorForJason = "";
		try {
			fileValidationMessages = new ArrayList<>();
			if(mtaFile != null) {
				for (Part part : getAllFilePartsFromMultifileUpload(mtaFile)) {
					String fileId = UUID.randomUUID().toString();
					String filename = storeFile(part, fileId);
					result.append(seperatorForJason + createIndicateAccessConditionFilesJson(fileId, queryId, filename, "MTA"));
					seperatorForJason = ",";
				}
			}
			if(otherAccessFile != null) {
				for (Part part : getAllFilePartsFromMultifileUpload(otherAccessFile)) {
					String fileId = UUID.randomUUID().toString();
					String filename = storeFile(part, fileId);
					result.append(seperatorForJason + createIndicateAccessConditionFilesJson(fileId, queryId, filename, "Other"));
					seperatorForJason = ",";
				}
			}
		} catch (Exception e) {
			logger.error("729f8d59add2-OwnerQueriesDetailBean ERROR-NG-0000054: Error uploading files for access condition.");
			e.printStackTrace();
		}
		result.append("]");
		return result.toString();
	}

	private String createIndicateAccessConditionFilesJson(String fileId, Integer queryId, String filename, String fileType) {
		return "{\"fileId\":\"" + fileId + "\",\"queryId\":\"" + queryId + "\",\"filename\":\"" + filename
				+ "\",\"fileType\":\"" + fileType + "\"}";
	}

	private String storeFile(Part part, String fileId) {
		try {
			String originalFileName = fileUtil.getOriginalFileNameFromPart(part);
			List<FacesMessage> errorFileMassages = fileUtil.validateFile(part, negotiator.getMaxUploadFileSize());
			fileValidationMessages.addAll(errorFileMassages);
			if (errorFileMassages == null || errorFileMassages.isEmpty()) {
				String storageFileName = fileUtil.getStorageFileName(queryId, fileId, originalFileName);
				fileUtil.saveQueryAttachment(part, storageFileName);
			}
			return originalFileName;
		} catch (Exception e) {
			logger.error("729f8d59add2-OwnerQueriesDetailBean ERROR-NG-0000055: Error uploading file.");
			e.printStackTrace();
			return "";
		}
	}

	private Collection<Part> getAllFilePartsFromMultifileUpload(Part part) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return request.getParts().stream().filter(p -> part.getName().equals(p.getName())).collect(Collectors.toList());
	}

	/*
	 * File Upload code block
	 */
	public String uploadAttachment() {
		if (!fileUploadBean.isFileToUpload())
			return "";

		fileUploadBean.createQueryAttachment();
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	public String uploadAttachmentPrivate(Integer offerFromBiobank) {
		if (!fileUploadBean.isFileToUpload())
			return "";

		fileUploadBean.createQueryAttachmentPrivate(offerFromBiobank);
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	public String removeAttachment() {
		boolean fileDeleted = fileUploadBean.removeAttachment();
		if(!fileDeleted) {
			return "";
		}
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	/*
	 * Getter / Setter for bean
	 */

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public FileUploadBean getFileUploadBean() {
		return fileUploadBean;
	}

	public void setFileUploadBean(FileUploadBean fileUploadBean) {
		this.fileUploadBean = fileUploadBean;
	}

	public void setQueries(List<OwnerQueryStatsDTO> queries) {
		this.queries = queries;
	}

	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
		fileUploadBean.setupQuery(queryId);
	}

	public Query getSelectedQuery() {
		return selectedQuery;
	}

	public void setSelectedQuery(Query selectedQuery) {
		this.selectedQuery = selectedQuery;
	}

	public List<CommentPersonDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentPersonDTO> comments) {
		this.comments = comments;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Flag getFlagFilter() {
		return flagFilter;
	}

	public void setFlagFilter(Flag flagFilter) {
		this.flagFilter = flagFilter;
	}

    public List<BiobankRecord> getAssociatedBiobanks() {
        return associatedBiobanks;
    }

    public void setAssociatedBiobanks(List<BiobankRecord> associatedBiobanks) {
        this.associatedBiobanks = associatedBiobanks;
    }

	public String getBiobankName(Integer biobankId) {
		return dataCache.getBiobankName(biobankId);
	}

	/**
	 * Tell the negotiator to expect results for the selected query from the connector
	 *
	 */
    public void participateInQuery(){
		try (Config config = ConfigFactory.get()) {
		    //TODO: have the biobanker decide which of his (possibly) many collections he wants to participate with.
            //      For now all will participate
            DbUtil.participateInQueryAndExpectResults(config, queryId, userBean.getCollections());

            // reload
            initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public List<OfferPersonDTO> getOfferPersonDTO() {
        return offerPersonDTO;
    }

    public void setOfferPersonDTO(List<OfferPersonDTO> offerPersonDTO) {
        this.offerPersonDTO = offerPersonDTO;
    }

	public String getHumanReadableQuery() {
		return humanReadableQuery;
	}

	public void setHumanReadableQuery(String humanReadableQuery) {
		this.humanReadableQuery = humanReadableQuery;
	}

	public boolean isNonConfidential() {
		return nonConfidential;
	}

	public void setNonConfidential(boolean nonConfidential) {
		this.nonConfidential = nonConfidential;
	}

	public List<List<OfferPersonDTO>> getListOfSampleOffers() {
		return listOfSampleOffers;
	}

	public void setListOfSampleOffers(List<List<OfferPersonDTO>> listOfSampleOffers) {
		this.listOfSampleOffers = listOfSampleOffers;
	}

	public Person getUserDataForResearcher(Integer researcherId) {
    	if(selectedQuery != null) {
			try (Config config = ConfigFactory.get()) {
				return DbUtil.getPersonDetails(config, researcherId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return null;
	}

	public RequestLifeCycleStatus getRequestLifeCycleStatus() {
		return requestLifeCycleStatus;
	}

	public String getRequestLifeCycleStatusHistory() {
		return requestLifeCycleStatus.getRequestLifecycleHistory(userBean.getPerson());
	}

	public String getNextCollectionLifecycleStatusStatus() {
		return nextCollectionLifecycleStatusStatus;
	}

	public void setNextCollectionLifecycleStatusStatus(String nextCollectionLifecycleStatusStatus) {
		this.nextCollectionLifecycleStatusStatus = nextCollectionLifecycleStatusStatus;
	}

	public Integer getNumberOfSamplesAvailable() {
		return numberOfSamplesAvailable;
	}

	public void setNumberOfSamplesAvailable(Integer numberOfSamplesAvailable) {
		this.numberOfSamplesAvailable = numberOfSamplesAvailable;
	}

	public String getIndicateAccessConditions() {
		return indicateAccessConditions;
	}

	public void setIndicateAccessConditions(String indicateAccessConditions) {
		this.indicateAccessConditions = indicateAccessConditions;
	}

	public String getShippedNumber() {
		return shippedNumber;
	}

	public void setShippedNumber(String shippedNumber) {
		this.shippedNumber = shippedNumber;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	public Integer getBiobankId() {
		return biobankId;
	}

	public void setBiobankId(Integer biobankId) {
		this.biobankId = biobankId;
	}

	public Integer getNumberOfPatientsAvailable() {
		return numberOfPatientsAvailable;
	}

	public void setNumberOfPatientsAvailable(Integer numberOfPatientsAvailable) {
		this.numberOfPatientsAvailable = numberOfPatientsAvailable;
	}

	public Part getMtaFile() {
		return mtaFile;
	}

	public void setMtaFile(Part mtaFile) {
		this.mtaFile = mtaFile;
	}

	public Part getDtaFile() {
		return dtaFile;
	}

	public void setDtaFile(Part dtaFile) {
		this.dtaFile = dtaFile;
	}

	public Part getOtherAccessFile() {
		return otherAccessFile;
	}

	public void setOtherAccessFile(Part otherAccessFile) {
		this.otherAccessFile = otherAccessFile;
	}

	public Boolean getIsTestRequest() {
		return isTestRequest;
	}

	public void setIsTestRequest(Boolean testRequest) {
		isTestRequest = testRequest;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getUnreadCommentCount() {
		return unreadCommentCount;
	}

	public void setUnreadCommentCount(int unreadCommentCount) {
		this.unreadCommentCount = unreadCommentCount;
	}

	public int getPrivateNegotiationCount() {
		return privateNegotiationCount;
	}

	public void setPrivateNegotiationCount(int privateNegotiationCount) {
		this.privateNegotiationCount = privateNegotiationCount;
	}

	public int getUnreadPrivateNegotiationCount() {
		return unreadPrivateNegotiationCount;
	}

	public void setUnreadPrivateNegotiationCount(int unreadPrivateNegotiationCount) {
		this.unreadPrivateNegotiationCount = unreadPrivateNegotiationCount;
	}

	public List<Person> getPersonList() {
		return personList;
	}

	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}

	public HashMap<String, List<CollectionLifeCycleStatus>> getSortedCollections() {
		return sortedCollections;
	}

	public List<String> getSortedCollectionsKeys() {
		return new ArrayList<String>(sortedCollections.keySet());
	}

	public List<CollectionLifeCycleStatus> getSortedCollectionsByKathegory(String key) {
		return sortedCollections.get(key);
	}
}
