package cloudgene.mapred.resources.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.App;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.YamlLoader;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.jobs.MapReduceJob;
import cloudgene.mapred.representations.JSONAnswer;
import cloudgene.mapred.util.S3Util;

public class RerunJob extends ServerResource {

	@Post
	public Representation post(Representation entity) {

		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		if (user != null) {

			// check aws credentials and s3 bucket
			if (user.isExportToS3()) {

				if (!S3Util.checkBucket(user.getAwsKey(),
						user.getAwsSecretKey(), user.getS3Bucket())) {

					return new JSONAnswer(
							"Your AWS-Credentials are wrong or your S3-Bucket doesn't exists.<br/><br/>Please update your AWS-Credentials.",
							false);

				}

			}

			JobQueue queue = JobQueue.getInstance();

			String jobId = form.getFirstValue("id");
			if (jobId != null) {

				// delete job from database
				JobDao dao = new JobDao();
				Job oldJob = dao.findById(jobId);

				String tool = jobId.split("-")[0];

				App app = YamlLoader.loadApp(tool);
				if (app.getMapred() != null) {

					try {

						MapReduceJob job = new MapReduceJob(app.getMapred());

						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyyMMdd-HHmmss");
						String name = tool + "-" + sdf.format(new Date());
						job.setId(name);
						job.setName(name);
						job.setUser(user);

						// parse params
						for (Parameter params : oldJob.getInputParams()) {
							String key = params.getId();
							String value = params.getValue();
							job.setInputParam(key, value);

						}
						queue.submit(job);

					} catch (Exception e) {

						return new JSONAnswer(e.getMessage(), false);

					}

				}

				return new JSONAnswer(
						"Your job was successfully added to the job queue.",
						true);
			}

		}

		getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("error");

	}

}
