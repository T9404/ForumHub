package com.example.integration;

import com.example.category.db.CategoryEntity;
import com.example.category.db.CategoryRepository;
import com.example.common.OrderSortingType;
import com.example.message.db.MessageRepository;
import com.example.message.dto.MessageFilter;
import com.example.topic.db.TopicEntity;
import com.example.topic.db.TopicRepository;
import com.example.integration.config.ContainerTest;
import com.example.error.response.ErrorResponseDto;
import com.example.message.controller.response.CreateMessageResponseDto;
import com.example.message.controller.response.GetMessageByContentDto;
import com.example.message.controller.response.MessageResponseDto;
import com.example.common.PageResponse;
import com.example.message.controller.request.CreateMessageRequestDto;
import com.example.message.controller.request.UpdateMessageRequestDto;
import com.example.topic.controller.request.GetMessageByTopicRequest;
import io.restassured.RestAssured;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.OffsetDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ContainerTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageTest {
    private static final UUID CREATOR_ID = UUID.fromString("c0407501-ccfa-4957-aa1e-b52912ec2c9b");

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        messageRepository.deleteAll();
        topicRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Ignore
    public void createMessage() {
        var topicId = createTopic();

        var request = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var message = messageRepository.findById(response.messageId()).orElseThrow();
        assertThat(message.getContent()).isEqualTo("content");
        assertThat(message.getCreatorId()).isEqualTo(CREATOR_ID);
        assertThat(message.getTopic().getTopicId()).isEqualTo(topicId);
    }

    @Test
    @Ignore
    public void createMessageWithFantomTopic() {
        var randomTopicId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9b");

        var request = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(randomTopicId)
                .creatorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Topic with id " + randomTopicId + " not found");
    }

    @Test
    @Ignore
    public void getMessageByContent() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByContentDto.builder()
                .content("content")
                .build();

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-content")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto[].class);

        assertThat(response).hasSize(1);
        assertThat(response[0].messageId()).isEqualTo(createResponse.messageId());
    }

    @Test
    @Ignore
    public void getMessageByContentNotFound() {
        var getMessageRequest = GetMessageByContentDto.builder()
                .content("content")
                .build();

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-content")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto[].class);

        assertThat(response).isEmpty();
    }

    @Test
    @Ignore
    public void getMessageByContentDouble() {
        var topicId = createTopic();

        var firstCreateRequest = CreateMessageRequestDto.builder()
                .content("content1")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var firstCreateResponse = given()
                .contentType("application/json")
                .body(firstCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var secondCreateRequest = CreateMessageRequestDto.builder()
                .content("content2")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var secondCreateResponse = given()
                .contentType("application/json")
                .body(secondCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByContentDto.builder()
                .content("content")
                .build();

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-content")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto[].class);

        assertThat(response).hasSize(2);

        assertThat(response[0].content()).isEqualTo("content1");
        assertThat(response[0].messageId()).isEqualTo(firstCreateResponse.messageId());

        assertThat(response[1].messageId()).isEqualTo(secondCreateResponse.messageId());
        assertThat(response[1].content()).isEqualTo("content2");
    }

    @Test
    @Ignore
    public void getMessageByContentCaseInsensitive() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByContentDto.builder()
                .content("CONTENT")
                .build();

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-content")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto[].class);

        assertThat(response).hasSize(1);
        assertThat(response[0].messageId()).isEqualTo(createResponse.messageId());
    }

    @Test
    @Ignore
    public void getMessageByContentEmpty() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByContentDto.builder()
                .content("")
                .build();

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-content")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto[].class);

        assertThat(response).hasSize(1);
        assertThat(response[0].messageId()).isEqualTo(createResponse.messageId());
    }

    @Test
    @Ignore
    public void testUpdateMessage() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var updateRequest = UpdateMessageRequestDto.builder()
                .content("new content")
                .messageId(createResponse.messageId())
                .build();

        var response = given()
                .contentType("application/json")
                .body(updateRequest)
                .patch("/api/v1/messages/updating")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto.class);

        assertThat(response.messageId()).isEqualTo(createResponse.messageId());
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    @Ignore
    public void testUpdateMessageWithTopic() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var newTopicId = createTopic();

        var updateRequest = UpdateMessageRequestDto.builder()
                .content("new content")
                .messageId(createResponse.messageId())
                .topicId(newTopicId)
                .build();

        var response = given()
                .contentType("application/json")
                .body(updateRequest)
                .patch("/api/v1/messages/updating")
                .then()
                .statusCode(200)
                .extract()
                .as(MessageResponseDto.class);

        var message = messageRepository.findById(response.messageId()).orElseThrow();
        assertThat(message.getTopic().getTopicId()).isEqualTo(newTopicId);
        assertThat(response.messageId()).isEqualTo(createResponse.messageId());
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    @Ignore
    public void testUpdateMessageWithFantomTopic() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var randomTopicId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9b");

        var updateRequest = UpdateMessageRequestDto.builder()
                .content("new content")
                .messageId(createResponse.messageId())
                .topicId(randomTopicId)
                .build();

        var response = given()
                .contentType("application/json")
                .body(updateRequest)
                .patch("/api/v1/messages/updating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Topic with id " + randomTopicId + " not found");
    }

    @Test
    @Ignore
    public void testUpdateMessageWithEmptyMessageId() {
        var updateRequest = UpdateMessageRequestDto.builder()
                .content("new content")
                .messageId(null)
                .build();

        var response = given()
                .contentType("application/json")
                .body(updateRequest)
                .patch("/api/v1/messages/updating")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Message id is required");
    }

    @Test
    @Ignore
    public void testUpdateMessageWithNotFoundMessageId() {
        var messageId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9b");

        var updateRequest = UpdateMessageRequestDto.builder()
                .content("new content")
                .messageId(messageId)
                .build();

        var response = given()
                .contentType("application/json")
                .body(updateRequest)
                .patch("/api/v1/messages/updating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Message with id " + updateRequest.messageId() + " not found");
    }

    @Test
    @Ignore
    public void testDeleteMessage() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        given()
                .delete("/api/v1/messages/deleting/" + createResponse.messageId())
                .then()
                .statusCode(200);

        assertThat(messageRepository.findById(createResponse.messageId())).isEmpty();
    }

    @Test
    @Ignore
    public void testDeleteMessageWithNotFoundMessageId() {
        var messageId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9b");

        var response = given()
                .delete("/api/v1/messages/deleting/" + messageId)
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Message with id " + messageId + " not found");
    }

    @Test
    @Ignore
    public void testGetAllMessage() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var messageFilter = MessageFilter.builder()
                .content("CONTENT")
                .build();

        var page = 0;
        var size = 10;

        var response = given()
                .contentType("application/json")
                .body(messageFilter)
                .get("/api/v1/messages/all?page=" + page + "&size=" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(response.getMetadata().getTotalElements()).isEqualTo(1);
        assertThat(response.getMetadata().getPage()).isZero();
        assertThat(response.getMetadata().getSize()).isEqualTo(10);

        assertThat(response.getContent().toString().contains(createResponse.messageId().toString())).isTrue();
    }

    @Test
    @Ignore
    public void testGetAllMessagesWithEmptyFilter() {
        var topicId = createTopic();

        var createRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        given()
                .contentType("application/json")
                .body(createRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200);

        var messageFilter = MessageFilter.builder().build();

        var page = 0;
        var size = 10;

        var response = given()
                .contentType("application/json")
                .body(messageFilter)
                .get("/api/v1/messages/all?page=" + page + "&size=" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(response.getMetadata().getTotalElements()).isEqualTo(1);
        assertThat(response.getMetadata().getPage()).isZero();
        assertThat(response.getMetadata().getSize()).isEqualTo(10);

        assertThat(response.getContent().toString().contains(createRequest.content())).isTrue();
    }

    @Test
    @Ignore
    public void testGetMessagesByTopic() {
        var topicId = createTopic();

        var firstCreateRequest = CreateMessageRequestDto.builder()
                .content("content")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var firstCreateResponse = given()
                .contentType("application/json")
                .body(firstCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var secondCreateRequest = CreateMessageRequestDto.builder()
                .content("content2")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var secondCreateResponse = given()
                .contentType("application/json")
                .body(secondCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByTopicRequest.builder()
                .topicId(topicId)
                .direction(OrderSortingType.DESC.getValue())
                .build();

        var page = 0;
        var size = 10;

        var response = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-topic?page=" + page + "&size=" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(response.getMetadata().getTotalElements()).isEqualTo(2);

        assertThat(response.getContent().toString().contains(firstCreateResponse.messageId().toString())).isTrue();
        assertThat(response.getContent().toString().contains(secondCreateResponse.messageId().toString())).isTrue();
    }

    @Test
    @Ignore
    public void testGetMessagesWithPagination() {
        var topicId = createTopic();

        var firstCreateRequest = CreateMessageRequestDto.builder()
                .content("content1")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var firstCreateResponse = given()
                .contentType("application/json")
                .body(firstCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var secondCreateRequest = CreateMessageRequestDto.builder()
                .content("content2")
                .topicId(topicId)
                .creatorId(CREATOR_ID)
                .build();

        var secondCreateResponse = given()
                .contentType("application/json")
                .body(secondCreateRequest)
                .post("/api/v1/messages/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateMessageResponseDto.class);

        var getMessageRequest = GetMessageByTopicRequest.builder()
                .topicId(topicId)
                .direction(OrderSortingType.DESC.getValue())
                .build();

        var page = 0;
        var size = 1;

        var firstPageResponse = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-topic?page=" + page + "&size=" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(firstPageResponse.getMetadata().getTotalElements()).isEqualTo(2);

        System.out.println(firstPageResponse.getContent().toString());
        assertThat(firstPageResponse.getContent().toString().contains(firstCreateResponse.messageId().toString())).isFalse();
        assertThat(firstPageResponse.getContent().toString().contains(secondCreateResponse.messageId().toString())).isTrue();

        page = 1;
        var secondPageResponse = given()
                .contentType("application/json")
                .body(getMessageRequest)
                .get("/api/v1/messages/getting-by-topic?page=" + page + "&size=" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(secondPageResponse.getMetadata().getTotalElements()).isEqualTo(2);

        assertThat(secondPageResponse.getContent().toString().contains(firstCreateResponse.messageId().toString())).isTrue();
        assertThat(secondPageResponse.getContent().toString().contains(secondCreateResponse.messageId().toString())).isFalse();
    }

    private UUID createTopic() {
        var category = CategoryEntity.builder()
                .name("Category")
                .creatorId(CREATOR_ID)
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .build();

        var savedCategory = categoryRepository.save(category);

        var topic = TopicEntity.builder()
                .name("Topic")
                .category(savedCategory)
                .creatorId(CREATOR_ID)
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .build();

        return topicRepository.save(topic).getTopicId();
    }
}
