package bednarz.glazer.sakowicz.userinfo;

import java.util.List;

public record UserInfoRequest(List<Long> usersId, String applicationName) {}
