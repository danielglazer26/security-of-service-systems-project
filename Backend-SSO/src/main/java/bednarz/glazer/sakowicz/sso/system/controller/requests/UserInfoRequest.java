package bednarz.glazer.sakowicz.sso.system.controller.requests;

import java.util.List;

public record UserInfoRequest(List<Long> usersId) {
}
