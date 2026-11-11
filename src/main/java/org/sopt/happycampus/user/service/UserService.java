package org.sopt.happycampus.user.service;

import org.sopt.happycampus.user.entity.UserEntity;
import org.sopt.happycampus.user.exception.UserNotFoundException;
import org.sopt.happycampus.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserEntity getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}
}
