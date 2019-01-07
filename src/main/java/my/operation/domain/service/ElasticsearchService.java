package my.operation.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import my.operation.domain.entity.Issue;
import my.operation.domain.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Properties;

public class ElasticsearchService implements ApplicationService {

    private static final String INDEX_TYPE = "default";
    private static final String ILLEGAL_PARAMETER_LOG = "%s is not supported for searching";
    private static final String SAVE_RESULT_LOG = "Save result for {} is {}";

    private Logger logger = LogManager.getLogger(this.getClass());
    private TransportClient elasticsearchClient;
    private ObjectMapper objectMapper;
    private String hostname;
    private int port;

    public ElasticsearchService(Properties properties) {
        String[] clusterNodes = properties.getProperty("elasticsearch.cluster-nodes").split(":");
        hostname = clusterNodes[0];
        port = Integer.valueOf(clusterNodes[1]);
        String userIndex = properties.getProperty("user.elasticsearch.index");
        String issueIndex = properties.getProperty("issue.elasticsearch.index");
        ElasticsearchIndex.USER.setIndexName(userIndex);
        ElasticsearchIndex.ISSUE.setIndexName(issueIndex);
    }

    @Override
    public void init() {
        try {
            elasticsearchClient = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(hostname), port));
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.registerSubtypes(User.class);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        } catch (UnknownHostException e) {
            String errMsg = String.format("An error occurred while starting up %s", ElasticsearchService.class);
            throw new RuntimeException(errMsg, e);
        }
    }

    @Override
    public void shutdown() {
        elasticsearchClient.close();
    }

    public <E> void save(E e) {
        try {
            ElasticsearchIndex elasticsearchIndex = ElasticsearchIndex.getIndex(e.getClass())
                    .orElseThrow(() -> new IllegalArgumentException(String.format(ILLEGAL_PARAMETER_LOG, e.getClass())));
            elasticsearchIndex.save(elasticsearchClient, objectMapper, e);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while trying to cache object of type %s for searching. The object is %s",
                    e.getClass(),
                    e.toString());
            this.logger.error(errMsg, exception);
        }
    }

    public enum ElasticsearchIndex {
        USER(User.class) {
            @Override
            void save(TransportClient elasticsearchClient, ObjectMapper objectMapper, Object object) {
                if (!(object instanceof User)) {
                    logger.warn("An attempt to save an object of type {} to the index {} has been blocked. " +
                            "The object {} cannot be searched",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    return;
                }

                try {
                    IndexResponse response = elasticsearchClient.prepareIndex()
                            .setIndex(this.getIndexName())
                            .setType(INDEX_TYPE)
                            .setId(((User) object).getId().toString())
                            .setSource(objectMapper.writeValueAsBytes(object), XContentType.JSON)
                            .get();
                    logger.info(SAVE_RESULT_LOG, ((User) object).getStaffId(), response);
                } catch (JsonProcessingException e) {
                    String errMsg = String.format("An error occurred while trying to cache object of type %s to the index %s for searching. The object is %s",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    logger.error(errMsg, e);
                }
            }

            @Override
            void delete(TransportClient elasticsearchClient, Object object) {
                if (!(object instanceof User)) {
                    logger.warn("An attempt to delete an object of type {} to the index {} has been blocked. The object {} cannot be searched",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    return;
                }

                DeleteResponse response = elasticsearchClient.prepareDelete()
                        .setIndex(this.getIndexName())
                        .setType(INDEX_TYPE)
                        .setId(((User) object).getId().toString())
                        .get();
                logger.info(SAVE_RESULT_LOG, ((User) object).getStaffId(), response);
            }
        },
        ISSUE(Issue.class) {
            @Override
            void save(TransportClient elasticsearchClient, ObjectMapper objectMapper, Object object) {
                if (!(object instanceof Issue)) {
                    logger.warn("An attempt to save an object of type {} to the index {} has been blocked. " +
                                    "The object {} cannot be searched",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    return;
                }

                try {
                    IndexResponse response = elasticsearchClient.prepareIndex()
                            .setIndex(this.getIndexName())
                            .setType(INDEX_TYPE)
                            .setId(((Issue) object).getId().toString())
                            .setSource(objectMapper.writeValueAsBytes(object), XContentType.JSON)
                            .get();
                    logger.info(SAVE_RESULT_LOG, ((Issue) object).getName(), response);
                } catch (JsonProcessingException e) {
                    String errMsg = String.format("An error occurred while trying to cache object of type %s to the index %s for searching. The object is %s",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    logger.error(errMsg, e);
                }
            }

            @Override
            void delete(TransportClient elasticsearchClient, Object object) {
                if (!(object instanceof Issue)) {
                    logger.warn("An attempt to delete an object of type {} to the index {} has been blocked. The object {} cannot be searched",
                            object.getClass(),
                            this.getIndexName(),
                            object.toString());
                    return;
                }

                DeleteResponse response = elasticsearchClient.prepareDelete()
                        .setIndex(this.getIndexName())
                        .setType(INDEX_TYPE)
                        .setId(((Issue) object).getId().toString())
                        .get();
                logger.info(SAVE_RESULT_LOG, ((Issue) object).getName(), response);
            }
        };

        static Logger logger = LogManager.getLogger(ElasticsearchIndex.class);
        private Class classOfObject;
        private String indexName;

        ElasticsearchIndex(Class classOfObject) {
            this.classOfObject = classOfObject;
        }

        static Optional<ElasticsearchIndex> getIndex(Class classOfObject) {
            for (ElasticsearchIndex elasticsearchIndex : ElasticsearchIndex.values()) {
                if (elasticsearchIndex.getClassOfObject().equals(classOfObject)) {
                    return Optional.of(elasticsearchIndex);
                }
            }

            return Optional.empty();
        }

        Class getClassOfObject() {
            return classOfObject;
        }

        String getIndexName() {
            return indexName;
        }

        void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        abstract void save(TransportClient elasticsearchClient, ObjectMapper objectMapper, Object object);
        abstract void delete(TransportClient elasticsearchClient, Object object);
    }
}
