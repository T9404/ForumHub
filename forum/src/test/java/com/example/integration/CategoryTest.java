package com.example.integration;

import com.example.core.category.db.CategoryRepository;
import com.example.core.topic.db.TopicEntity;
import com.example.core.topic.db.TopicRepository;
import com.example.integration.config.ContainerTest;
import com.example.rest.category.response.CategoryHierarchyDto;
import com.example.rest.category.response.CategoryResponseDto;
import com.example.rest.category.response.CreateCategoryResponseDto;
import com.example.rest.category.request.GetCategoryByNameRequest;
import com.example.rest.error.response.ErrorResponseDto;
import com.example.public_interface.page.PageResponse;
import com.example.rest.category.request.CreateCategoryRequestDto;
import com.example.rest.category.request.GetCategoryRequest;
import com.example.rest.category.request.UpdateCategoryRequestDto;
import io.restassured.RestAssured;
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
public class CategoryTest {
    private static final UUID CREATOR_ID = UUID.fromString("c0407501-ccfa-4957-aa1e-b52912ec2c9b");
    private static final UUID PREVIOUS_CATEGORY_ID = UUID.fromString("c0407501-ccfa-4957-aa1e-b52912ec2c9e");
    private static final String CATEGORY_NAME = "Category name";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        topicRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void testCreateCategory() {
        var request = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = categoryRepository.findById(response.categoryId()).orElseThrow();
        assertThat(category.getName()).isEqualTo(CATEGORY_NAME);
        assertThat(category.getCreatorId()).isEqualTo(CREATOR_ID);
    }

    @Test
    public void testCreateCategoryWithExistTopics() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = categoryRepository.findById(firstResponse.categoryId()).orElseThrow();
        var topic = new TopicEntity();
        topic.setCategory(category);
        topic.setCreatorId(CREATOR_ID);
        topic.setCreatedAt(OffsetDateTime.now());
        topic.setName("Topic name");
        topicRepository.save(topic);

        var secondRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .previousCategoryId(firstResponse.categoryId())
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(secondResponse.message()).isEqualTo("Category with id " + firstResponse.categoryId() + " has topics");
    }

    @Test
    public void testCreateWithFantomPreviousCategory() {
        var request = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .previousCategoryId(PREVIOUS_CATEGORY_ID)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Category with id " + request.previousCategoryId() + " not found");
    }

    @Test
    public void testCreateWithValidPreviousCategory() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var secondRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .previousCategoryId(firstResponse.categoryId())
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = categoryRepository.findById(secondResponse.categoryId()).orElseThrow();
        assertThat(category.getPreviousCategoryId()).isEqualTo(firstResponse.categoryId());
    }

    @Test
    public void testUpdateCategory() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var secondRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var thirdRequest = UpdateCategoryRequestDto.builder()
                .categoryId(secondResponse.categoryId())
                .previousCategoryId(firstResponse.categoryId())
                .name("New category name")
                .build();

        var thirdResponse = given()
                .contentType("application/json")
                .body(thirdRequest)
                .when()
                .patch("/api/v1/categories/updating")
                .then()
                .statusCode(200)
                .extract()
                .as(CategoryResponseDto.class);

        var category = categoryRepository.findById(thirdResponse.categoryId()).orElseThrow();
        assertThat(category.getPreviousCategoryId()).isEqualTo(firstResponse.categoryId());
        assertThat(category.getName()).isEqualTo("New category name");
    }

    @Test
    public void testUpdateCategoryWithFantomPreviousCategory() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var secondRequest = UpdateCategoryRequestDto.builder()
                .categoryId(firstResponse.categoryId())
                .previousCategoryId(PREVIOUS_CATEGORY_ID)
                .name("New category name")
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .patch("/api/v1/categories/updating")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(secondResponse.message()).isEqualTo("Category with id " + secondRequest.previousCategoryId() + " not found");
    }

    @Test
    public void testUpdateCategoryWithExistTopics() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = categoryRepository.findById(firstResponse.categoryId()).orElseThrow();
        var topic = new TopicEntity();
        topic.setCategory(category);
        topic.setCreatorId(CREATOR_ID);
        topic.setCreatedAt(OffsetDateTime.now());
        topic.setName("Topic name");
        topicRepository.save(topic);

        var secondRequest = UpdateCategoryRequestDto.builder()
                .categoryId(firstResponse.categoryId())
                .previousCategoryId(firstResponse.categoryId())
                .name("New category name")
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .patch("/api/v1/categories/updating")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(secondResponse.message()).isEqualTo("Category with id " + firstResponse.categoryId() + " has topics");
    }

    @Test
    public void testDeleteCategory() {
        var request = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        given()
                .when()
                .delete("/api/v1/categories/deleting/" + response.categoryId())
                .then()
                .statusCode(200);

        assertThat(categoryRepository.findById(response.categoryId())).isEmpty();
    }

    @Test
    public void testDeleteCategoryWithExistTopics() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = categoryRepository.findById(firstResponse.categoryId()).orElseThrow();
        var topic = new TopicEntity();
        topic.setCategory(category);
        topic.setCreatorId(CREATOR_ID);
        topic.setCreatedAt(OffsetDateTime.now());
        topic.setName("Topic name");
        topicRepository.save(topic);

        var secondResponse = given()
                .when()
                .delete("/api/v1/categories/deleting/" + firstResponse.categoryId())
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(secondResponse.message()).isEqualTo("Category with id " + firstResponse.categoryId() + " has topics");
        assertThat(categoryRepository.findById(firstResponse.categoryId())).isNotEmpty();
    }

    @Test
    public void testDeleteFantomCategory() {
        var response = given()
                .when()
                .delete("/api/v1/categories/deleting/" + PREVIOUS_CATEGORY_ID)
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Category with id " + PREVIOUS_CATEGORY_ID + " not found");
    }

    @Test
    public void testGetCategory() {
        var request = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var category = given()
                .when()
                .get("/api/v1/categories/" + response.categoryId())
                .then()
                .statusCode(200)
                .extract()
                .as(CategoryResponseDto.class);

        assertThat(category.categoryId()).isEqualTo(response.categoryId());
        assertThat(category.name()).isEqualTo(CATEGORY_NAME);
        assertThat(category.createdAt()).isBefore(OffsetDateTime.now());
        assertThat(category.modificationAt()).isBefore(OffsetDateTime.now());
        assertThat(category.creatorId()).isEqualTo(CREATOR_ID);
    }

    @Test
    public void testGetCategoryWithFantomId() {
        var response = given()
                .when()
                .get("/api/v1/categories/" + PREVIOUS_CATEGORY_ID)
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponseDto.class);

        assertThat(response.message()).isEqualTo("Category with id " + PREVIOUS_CATEGORY_ID + " not found");
    }

    @Test
    public void testGetAllCategoriesWithHierarchy() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var secondRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name("FIRST CHILD CATEGORY NAME")
                .previousCategoryId(firstResponse.categoryId())
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var thirdRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name("SECOND CHILD CATEGORY NAME")
                .previousCategoryId(firstResponse.categoryId())
                .build();

        var thirdResponse = given()
                .contentType("application/json")
                .body(thirdRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var getCategoryRequest = GetCategoryRequest.builder()
                .topicDirection("asc")
                .topicSorting("name")
                .build();

        var categories = given()
                .contentType("application/json")
                .body(getCategoryRequest)
                .when()
                .get("/api/v1/categories/hierarchy")
                .then()
                .statusCode(200)
                .extract()
                .as(CategoryHierarchyDto[].class);

        assertThat(categories[0].categoryId()).isEqualTo(firstResponse.categoryId());

        assertThat(categories[0].children()).hasSize(2);
        assertThat(categories[0].children().getFirst().categoryId()).isEqualTo(secondResponse.categoryId());
        assertThat(categories[0].children().getFirst().name()).isEqualTo("FIRST CHILD CATEGORY NAME");
        assertThat(categories[0].children().getFirst().createdAt()).isBefore(OffsetDateTime.now());
        assertThat(categories[0].children().getFirst().modificationAt()).isBefore(OffsetDateTime.now());
        assertThat(categories[0].children().getFirst().creatorId()).isEqualTo(CREATOR_ID);

        assertThat(categories[0].children().get(1).categoryId()).isEqualTo(thirdResponse.categoryId());
        assertThat(categories[0].children().get(1).name()).isEqualTo("SECOND CHILD CATEGORY NAME");
        assertThat(categories[0].children().get(1).createdAt()).isBefore(OffsetDateTime.now());
        assertThat(categories[0].children().get(1).modificationAt()).isBefore(OffsetDateTime.now());
        assertThat(categories[0].children().get(1).creatorId()).isEqualTo(CREATOR_ID);

    }

    @Test
    public void getAllCategoriesByNameWithDifferentRegister() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name("Category name")
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var getRequestByName = GetCategoryByNameRequest.builder()
                .name("CATEGORY")
                .build();

        var getResponseByName = given()
                .contentType("application/json")
                .body(getRequestByName)
                .when()
                .get("/api/v1/categories/getting-by-name")
                .then()
                .statusCode(200)
                .extract()
                .as(CategoryResponseDto[].class);

        assertThat(getResponseByName).hasSize(1);
        assertThat(getResponseByName[0].categoryId()).isEqualTo(firstResponse.categoryId());
        assertThat(getResponseByName[0].name()).isEqualTo(CATEGORY_NAME);
        assertThat(getResponseByName[0].createdAt()).isBefore(OffsetDateTime.now());
        assertThat(getResponseByName[0].modificationAt()).isBefore(OffsetDateTime.now());
        assertThat(getResponseByName[0].creatorId()).isEqualTo(CREATOR_ID);
    }

    @Test
    public void getAllCategories() {
        var createRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name(CATEGORY_NAME)
                .build();
        var createResponse = given()
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        int page = 0;
        int size = 10;

        var getAllResponse = given()
                .when()
                .get("/api/v1/categories/all/" + page + "/" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        System.out.println(getAllResponse);
        assertThat(getAllResponse.getMetadata().getTotalElements()).isEqualTo(1);
        assertThat(getAllResponse.getContent().getFirst().toString().contains(createResponse.categoryId().toString())).isTrue();
        assertThat(getAllResponse.getMetadata().getPage()).isEqualTo(page);
        assertThat(getAllResponse.getMetadata().getSize()).isEqualTo(size);
    }

    @Test
    public void testGetAllCategoryWithPagination() {
        var firstRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name("First")
                .build();

        var firstResponse = given()
                .contentType("application/json")
                .body(firstRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var secondRequest = CreateCategoryRequestDto.builder()
                .creatorId(CREATOR_ID)
                .name("Second")
                .previousCategoryId(firstResponse.categoryId())
                .build();

        var secondResponse = given()
                .contentType("application/json")
                .body(secondRequest)
                .when()
                .post("/api/v1/categories/creating")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateCategoryResponseDto.class);

        var page = 0;
        var size = 1;

        var firstGetAllCategoriesResponse = given()
                .when()
                .get("/api/v1/categories/all/" + page + "/" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        assertThat(firstGetAllCategoriesResponse.getMetadata().getTotalElements()).isEqualTo(2);
        assertThat(firstGetAllCategoriesResponse.getContent().toString().contains(firstResponse.categoryId().toString())).isTrue();
        assertThat(firstGetAllCategoriesResponse.getContent().toString().contains(secondResponse.categoryId().toString())).isFalse();

        page = 1;
        size = 1;

        var secondGetAllCategoriesResponse = given()
                .when()
                .get("/api/v1/categories/all/" + page + "/" + size)
                .then()
                .statusCode(200)
                .extract()
                .as(PageResponse.class);

        System.out.println(secondGetAllCategoriesResponse.getContent());
        assertThat(secondGetAllCategoriesResponse.getMetadata().getTotalElements()).isEqualTo(2);
        assertThat(secondGetAllCategoriesResponse.getContent().toString().contains(secondResponse.categoryId().toString())).isTrue();
    }
}
