package cloudgene.mapred.util;

import org.restlet.routing.*;
import org.restlet.Request;
import org.restlet.Response;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;

public class LoginFilter extends Filter {

	private String loginPage;

	private String[] protectedRequests;

	public LoginFilter(String loginPage, String[] protectedRequests) {
		this.loginPage = loginPage;
		this.protectedRequests = protectedRequests;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {

		String path = request.getResourceRef().getPath();

		if (path.toLowerCase().equals(loginPage)) {
			String token = request.getCookies().getFirstValue(
					UserSessions.COOKIE_NAME);
			if (token != null) {
				UserSessions sessions = UserSessions.getInstance();
				User user = sessions.getUserByToken(token);
				if (user != null) {
					response.redirectTemporary("/start.html");
					return STOP;
				}
			}
		}

		if (isProtected(path)) {

			String token = request.getCookies().getFirstValue(
					UserSessions.COOKIE_NAME);

			if (token == null) {
				response.redirectTemporary(loginPage);
				return STOP;
			} else {
				UserSessions sessions = UserSessions.getInstance();
				User user = sessions.getUserByToken(token);
				if (user == null) {
					response.redirectTemporary(loginPage);
					return STOP;
				}
			}

		}

		return CONTINUE;
	}

	private boolean isProtected(String path) {
		for (String protectedRequest : protectedRequests) {
			if (path.toLowerCase().equals(protectedRequest)) {
				return true;
			}
		}
		return false;
	}
}
