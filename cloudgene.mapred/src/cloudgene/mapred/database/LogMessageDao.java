package cloudgene.mapred.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.Step;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.LogMessage;

public class LogMessageDao extends Dao {

	private static final Log log = LogFactory.getLog(LogMessageDao.class);

	public boolean insert(LogMessage logMessage) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into log_messages (time, type, message, step_id) ");
		sql.append("values (?,?,?,?)");

		try {

			Object[] params = new Object[4];
			params[0] = System.currentTimeMillis();
			params[1] = logMessage.getType();
			params[2] = logMessage.getMessage();
			params[3] = logMessage.getStep().getId();
System.out.println(logMessage.getStep().getId());
			update(sql.toString(), params);

			connection.commit();

			log.info("insert log messages successful.");

		} catch (SQLException e) {
			log.error("insert log messages failed.", e);
			return false;
		}

		return true;
	}

	public List<LogMessage> findAllByStep(Step step) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from log_messages ");
		sql.append("where step_id = ? ");
		sql.append("order by time ");

		Object[] params = new Object[1];
		params[0] = step.getId();

		List<LogMessage> result = new Vector<LogMessage>();

		try {

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				LogMessage status = new LogMessage();
				status.setTime(rs.getLong("time"));
				status.setStep(step);
				status.setType(rs.getInt("type"));
				status.setMessage(rs.getString("message"));

				result.add(status);
			}
			rs.close();

			log.info("find all log messages successful. results: "
					+ result.size());

			return result;
		} catch (SQLException e) {
			log.error("find all log messages failed", e);
			return null;
		}
	}

}
