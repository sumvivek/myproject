package com.vikash.repository;
import javax.validation.constraints.NotNull;

import com.vikash.Exception.ImageSearchException;
import com.vikash.modal.User;

import java.util.List;
public interface ImageSearch {
	public boolean isEnabled();

    public List<User> search(@NotNull String username) throws ImageSearchException;

}
