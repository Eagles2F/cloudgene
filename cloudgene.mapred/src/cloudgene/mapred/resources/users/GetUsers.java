package cloudgene.mapred.resources.users;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.UserDao;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class GetUsers extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			UserDao dao = new UserDao();
			List<User> users = dao.findAll();

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "password" });

			JSONArray jsonArray = JSONArray.fromObject(users, config);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}
	}

}
