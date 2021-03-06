package cloudgene.mapred.resources.apps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sf.json.JSONArray;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.App;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.YamlLoader;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.Settings;

public class GetAppParams extends ServerResource {

	@Post
	public Representation post(Representation entity) {
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		Form form = new Form(entity);

		if (user != null) {

			App app = YamlLoader.loadApp(form.getFirstValue("tool"));

			List<Parameter> params = app.getMapred().getInputs();

			JSONArray jsonArray = JSONArray.fromObject(params);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}
	}

	@Get
	public Representation get(Representation entity) {
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String filename = Settings.getInstance().getApp();

			App app;
			try {
				app = YamlLoader.loadAppFromFile(filename);
			} catch (IOException e) {
				e.printStackTrace();
				return new StringRepresentation("Error");
			}

			List<Parameter> params = app.getMapred().getInputs();

			JSONArray jsonArray = JSONArray.fromObject(params);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}
	}

}
