package cloudgene.mapred.resources.apps;

import java.io.IOException;

import net.sf.json.JSONArray;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.Repository;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;

import com.esotericsoftware.yamlbeans.YamlException;

public class GetAppsFromRepository extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			Repository repo = new Repository("http://cloudgene.uibk.ac.at/apps");
			try {
				repo.load();
			} catch (YamlException e) {
				e.printStackTrace();
				return new StringRepresentation("error");
			} catch (IOException e) {
				e.printStackTrace();
				return new StringRepresentation("error");
			}

			// TODO: exclude all extjstreeitem properties

			JSONArray jsonArray = JSONArray.fromObject(repo.getApps());

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}

	}

}
