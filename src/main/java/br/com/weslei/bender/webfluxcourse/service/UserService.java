package br.com.weslei.bender.webfluxcourse.service;

import br.com.weslei.bender.webfluxcourse.entity.User;
import br.com.weslei.bender.webfluxcourse.mapper.UserMapper;
import br.com.weslei.bender.webfluxcourse.model.request.UserRequest;
import br.com.weslei.bender.webfluxcourse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request){
        return userRepository.save(mapper.toEntity(request));
    }
}
