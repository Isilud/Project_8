package com.openclassrooms.tourguide.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

import com.openclassrooms.tourguide.model.User;

@Repository
public class InMemoryUserRepository implements UserRepository {

	private final Map<String, User> internalUserMap = new HashMap<>();

	@Override
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	@Override
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	@Override
	public void add(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	@Override
	public void delete(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.remove(user.getUserName());
		}
	}

}