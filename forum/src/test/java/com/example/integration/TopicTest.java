package com.example.integration;

import com.example.core.category.db.CategoryEntity;
import com.example.core.category.db.CategoryRepository;
import com.example.core.topic.db.TopicRepository;
import com.example.core.topic.enums.TopicSorting;
import com.example.integration.config.ContainerTest;
import com.example.public_interface.error.ErrorResponseDto;
import com.example.public_interface.page.PageResponse;
import com.example.public_interface.topic.CreateTopicResponseDto;
import com.example.public_interface.topic.GetTopicByNameDto;
import com.example.public_interface.topic.TopicResponseDto;
import com.example.rest.controller.category.dto.CreateCategoryRequestDto;
import com.example.rest.controller.topic.dto.GetAllTopicsRequestDto;
import com.example.rest.controller.topic.dto.TopicRequestDto;
import com.example.rest.controller.topic.dto.UpdateTopicRequestDto;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ContainerTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicTest {
    private static final UUID CREATOR_ID = UUID.fromString("c0407501-ccfa-4957-aa1e-b52912ec2c9b");
    private static final String TOPIC_NAME = "Topic";

    @LocalServerPort
    private Integer port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        topicRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void createTopicWithouCategory() {
        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Category id cannot be null");
    }

    @Test
    public void createTopic() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var topic = topicRepository.findById(response.topicId()).orElseThrow();

        assertThat(topic.getName()).isEqualTo(TOPIC_NAME);
        assertThat(topic.getCategory().getCategoryId()).isEqualTo(categoryId);
        assertThat(topic.getCreatorId()).isEqualTo(CREATOR_ID);
        assertThat(topic.getCreatedAt()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(topic.getModificationAt()).isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    public void createTopicWithInvalidCategoryHierarchy() {
        var categoryId = createCategory();

        var createChildCategory = CreateCategoryRequestDto.builder()
                .name("SubCategory")
                .creatorId(CREATOR_ID)
                .previousCategoryId(categoryId)
                .build();

        given()
                .contentType("application/json")
                .body(createChildCategory)
                .when()
                .post("api/v1/categories/creating")
                .then()
                .statusCode(200);

        var createTopicRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createTopicRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Category with id " + categoryId + " has children");
    }

    @Test
    public void testUpdateTopic() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var updateRequest = UpdateTopicRequestDto.builder()
                .topicId(response.topicId())
                .name("New name")
                .build();

        given()
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .patch("api/v1/topics/updating")
                .then()
                .statusCode(200);

        var topic = topicRepository.findById(response.topicId()).orElseThrow();

        assertThat(topic.getName()).isEqualTo("New name");
    }

    @Test
    public void testUpdateTopicWithNotExistsCategory() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var randomCategoryId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9a");

        var updateRequest = UpdateTopicRequestDto.builder()
                .topicId(response.topicId())
                .categoryId(randomCategoryId)
                .build();

        var errorResponse = given()
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .patch("api/v1/topics/updating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(errorResponse.message()).isEqualTo("Category with id " + updateRequest.categoryId() + " not found");
    }

    @Test
    public void testUpdateTopicCategoryId() {
        var categoryId = createCategory();
        var newCategoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var updateRequest = UpdateTopicRequestDto.builder()
                .topicId(response.topicId())
                .categoryId(newCategoryId)
                .build();

        given()
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .patch("api/v1/topics/updating")
                .then()
                .statusCode(200);

        var topic = topicRepository.findById(response.topicId()).orElseThrow();

        assertThat(topic.getCategory().getCategoryId()).isEqualTo(newCategoryId);
    }

    @Test
    public void testDeleteTopic() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        given()
                .contentType("application/json")
                .when()
                .delete("api/v1/topics/deleting/" + response.topicId())
                .then()
                .statusCode(200);

        assertThat(topicRepository.findById(response.topicId())).isEmpty();
    }

    @Test
    public void testDeleteFantomTopic() {
        var randomTopicId = UUID.fromString("a0407501-ccfa-4957-aa1e-b52912ec2c9a");
        var response = given()
                .contentType("application/json")
                .when()
                .delete("api/v1/topics/deleting/" + randomTopicId)
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Topic with id " + randomTopicId + " not found");
    }

    @Test
    public void testGetTopicByName() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var getTopicByNameRequest = GetTopicByNameDto.builder()
                .name(TOPIC_NAME)
                .build();

        var getTopicByNameResponse = given()
                .contentType("application/json")
                .body(getTopicByNameRequest)
                .when()
                .get("api/v1/topics/getting-by-name")
                .then()
                .statusCode(200)
                .extract()
                .as(TopicResponseDto[].class);

        assertThat(Arrays.stream(getTopicByNameResponse).anyMatch(topic -> topic.topicId().equals(response.topicId()))).isTrue();
    }

    @Test
    public void testGetAllTopic() {
        var categoryId = createCategory();

        var createRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var getTopicAll = GetAllTopicsRequestDto.builder()
                .topicSorting(TopicSorting.NAME.name())
                .orderSortingType("asc")
                .page(0)
                .size(10)
                .build();

        var getTopicByNameResponse = given()
                .contentType("application/json")
                .body(getTopicAll)
                .when()
                .get("api/v1/topics/all")
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(getTopicByNameResponse.getContent().size()).isEqualTo(1);
        assertThat(getTopicByNameResponse.getContent().toString().contains(response.topicId().toString())).isTrue();
    }

    @Test
    public void testGetAllTopicWithPagination() {
        var categoryId = createCategory();

        var firstCreateRequest = TopicRequestDto.builder()
                .name(TOPIC_NAME)
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var firstCreateResponse = given()
                .contentType("application/json")
                .body(firstCreateRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var secondCreateRequest = TopicRequestDto.builder()
                .name("Second topic")
                .categoryId(categoryId)
                .authorId(CREATOR_ID)
                .build();

        var secondCreateResponse = given()
                .contentType("application/json")
                .body(secondCreateRequest)
                .when()
                .post("api/v1/topics/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateTopicResponseDto.class);

        var firstGetTopicAllRequest = GetAllTopicsRequestDto.builder()
                .topicSorting(TopicSorting.NAME.name())
                .orderSortingType("asc")
                .page(0)
                .size(1)
                .build();

        var firstGetTopicAllResponse = given()
                .contentType("application/json")
                .body(firstGetTopicAllRequest)
                .when()
                .get("api/v1/topics/all")
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(firstGetTopicAllResponse.getMetadata().getTotalElements()).isEqualTo(2);
        assertThat(firstGetTopicAllResponse.getContent().toString().contains(firstCreateResponse.topicId().toString())).isFalse();
        assertThat(firstGetTopicAllResponse.getContent().toString().contains(secondCreateResponse.topicId().toString())).isTrue();

        var secondGetTopicAllRequest = GetAllTopicsRequestDto.builder()
                .topicSorting(TopicSorting.NAME.name())
                .orderSortingType("asc")
                .page(1)
                .size(1)
                .build();

        var secondGetTopicAllResponse = given()
                .contentType("application/json")
                .body(secondGetTopicAllRequest)
                .when()
                .get("api/v1/topics/all")
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(secondGetTopicAllResponse.getMetadata().getTotalElements()).isEqualTo(2);
        assertThat(secondGetTopicAllResponse.getContent().toString().contains(firstCreateResponse.topicId().toString())).isTrue();
        assertThat(secondGetTopicAllResponse.getContent().toString().contains(secondCreateResponse.topicId().toString())).isFalse();
    }

    private UUID createCategory() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Category")
                .creatorId(CREATOR_ID)
                .createdAt(OffsetDateTime.now())
                .modificationAt(OffsetDateTime.now())
                .build();

        return categoryRepository.save(category).getCategoryId();
    }
}
