package cloudgene.mapred.steps;

import java.util.List;
import java.util.Vector;

import cloudgene.mapred.apps.Step;
import cloudgene.mapred.jobs.CloudgeneContext;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

public class MapReduce extends Hadoop {

	@Override
	public boolean run(CloudgeneContext context) {

		String hadoopPath = Settings.getInstance().getHadoopPath();
		String hadoop = FileUtil.path(hadoopPath, "bin", "hadoop");
		String streamingJar = Settings.getInstance().getStreamingJar();

		Step step = context.getStep();

		// params
		String paramsString = step.getParams();
		String[] params = context.resolveParams(paramsString.split(" "));

		// hadoop jar or streaming
		List<String> command = new Vector<String>();

		command.add(hadoop);
		command.add("jar");

		if (step.getJar() != null) {

			// classical
			command.add(step.getJar());

		} else {

			// streaming

			if (Settings.getInstance().isStreaming()) {

				command.add(streamingJar);

			} else {

				// throw new Exception(
				// "Streaming mode is disabled.\nPlease specify the streaming-jar file in config/settings.yaml to run this job.");

			}

		}

		for (String tile : params) {
			command.add(tile.trim());
		}

		// mapper and reducer

		if (step.getJar() == null) {

			if (step.getMapper() != null) {

				String tiles[] = step.getMapper().split(" ", 2);
				String filename = tiles[0];

				command.add("-mapper");

				if (tiles.length > 1) {
					String params2 = tiles[1];
					command.add(filename + " " + params2);
				} else {
					command.add(filename);
				}

			}

			if (step.getReducer() != null) {

				String tiles[] = step.getReducer().split(" ", 2);
				String filename = tiles[0];

				command.add("-reducer");

				if (tiles.length > 1) {
					String params2 = tiles[1];
					command.add(filename + " " + params2);
				} else {
					command.add(filename);
				}

			}

		}

		try {
			return executeCommand(command, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
