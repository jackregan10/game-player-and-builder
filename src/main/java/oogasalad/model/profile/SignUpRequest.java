package oogasalad.model.profile;

import java.io.File;

public record SignUpRequest(String username, String password, String firstName, String lastName, String bio, File pfp) {

}

