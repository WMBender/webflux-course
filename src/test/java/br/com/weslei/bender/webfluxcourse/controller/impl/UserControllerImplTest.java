package br.com.weslei.bender.webfluxcourse.controller.impl;

import br.com.weslei.bender.webfluxcourse.entity.User;
import br.com.weslei.bender.webfluxcourse.mapper.UserMapper;
import br.com.weslei.bender.webfluxcourse.model.request.UserRequest;
import br.com.weslei.bender.webfluxcourse.model.response.UserResponse;
import br.com.weslei.bender.webfluxcourse.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String ID = "123";
    public static final String NAME = "Weslei";
    public static final String EMAIL = "wesleibender@mail.com";
    public static final String INVALID_EMAIL = "wesleimail.com";
    public static final String PASSWORD = "abcde";
    public static final String INVALID_PASSWORD = "ab";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    @DisplayName("Test endpoint with success")
    void saveWithSuccess() {
        UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);

        when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        verify(service).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint with bad request at name")
    void saveWithBlankSpaceAtTheEndOfTheUserName() {
        UserRequest request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation Error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot contain blank spaces at the start or end of the string");

    }

    @Test
    @DisplayName("Test endpoint without value at name")
    void saveWithNullUserName() {
        UserRequest request = new UserRequest(null, EMAIL, PASSWORD);

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation Error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("Must not be null or empty");

    }

    @Test
    @DisplayName("Test endpoint with bad request at email")
    void saveWithInvalidEmail() {
        UserRequest request = new UserRequest(NAME, INVALID_EMAIL, PASSWORD);

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation Error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("email")
                .jsonPath("$.errors[0].message").isEqualTo("Invalid email");

    }

    @Test
    @DisplayName("Test endpoint with bad request at password")
    void saveWithInvalidPassword() {
        UserRequest request = new UserRequest(NAME, EMAIL, INVALID_PASSWORD);

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation Error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("password")
                .jsonPath("$.errors[0].message").isEqualTo("Must be between 3 and 50 characters");

    }

    @Test
    @DisplayName("Test find by id endpoint with success")
    void findWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(service.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri("/users/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service.findById(anyString()));
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test find all endpoint with success")
    void findAllWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);

        verify(service.findAll());
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test update endpoint with success")
    void updateWithSuccess() {
        UserRequest request = new UserRequest(NAME, EMAIL, INVALID_PASSWORD);
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(service.update(anyString(), any(UserRequest.class)))
                .thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch().uri("/users/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service.update(anyString(), any(UserRequest.class)));
        verify(mapper).toResponse(any(User.class));

    }

    @Test
    @DisplayName("Test delete endpoint with success")
    void deleteWithSuccess() {

        when(service.delete(ID)).thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete().uri("/users/" + ID)
                .exchange()
                .expectStatus().isOk();

        verify(service).delete(ID);
    }
}