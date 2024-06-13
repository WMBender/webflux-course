package br.com.weslei.bender.webfluxcourse.controller.impl;

import br.com.weslei.bender.webfluxcourse.controller.UserController;
import br.com.weslei.bender.webfluxcourse.mapper.UserMapper;
import br.com.weslei.bender.webfluxcourse.model.request.UserRequest;
import br.com.weslei.bender.webfluxcourse.model.response.UserResponse;
import br.com.weslei.bender.webfluxcourse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserControllerImpl implements UserController {

    private final UserService service;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<Mono<Void>> save(UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(request).then());
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> find(String id) {

        return  ResponseEntity.ok().body(
                service.findById(id).map(mapper::toResponse)
        );

    }

    @Override
    public ResponseEntity<Flux<UserResponse>> findAll() {
        return ResponseEntity.ok().body(
                service.findAll().map(mapper::toResponse)
        );
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> update(String id, UserRequest request) {

        return ResponseEntity.ok().body(
                service.update(id, request).map(mapper::toResponse)
        );
    }

    @Override
    public ResponseEntity<Mono<Void>> delete(String id) {
        return ResponseEntity.ok().body(
                service.delete(id).then()
        );
    }
}
