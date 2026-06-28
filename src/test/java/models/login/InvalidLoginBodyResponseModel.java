package models.login;

import java.util.List;

public record InvalidLoginBodyResponseModel(List<String> username, List<String> password) {
}
