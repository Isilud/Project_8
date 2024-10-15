package com.openclassrooms.tourguide.repository;

import java.util.List;

import com.openclassrooms.tourguide.model.User;

public interface UserRepository {

    public User getUser(String userName);

    public List<User> getAllUsers();

    public void add(User user);
}