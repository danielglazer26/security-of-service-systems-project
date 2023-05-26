package bednarz.glazer.sakowicz.backendapp2.userinfo;

import java.util.List;

public record UserInfoRequest(List<Long> usersId, String applicationName) {}
