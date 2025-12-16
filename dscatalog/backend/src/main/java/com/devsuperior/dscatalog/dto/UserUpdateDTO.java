package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO{

    private String password;

    public UserUpdateDTO() {}

    public UserUpdateDTO(String password) {
        this.password = password;
    }

    public UserUpdateDTO(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
        this.password = password;
    }

    public UserUpdateDTO(User entity) {
        super(entity);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
