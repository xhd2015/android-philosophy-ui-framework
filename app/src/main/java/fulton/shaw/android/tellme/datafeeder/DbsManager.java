package fulton.shaw.android.tellme.datafeeder;

import java.sql.Connection;
import java.util.HashMap;

public interface DbsManager {
	
	public Connection connectDatabase(String url,String user,String password);
	
	public void closeConnection(Connection connection);
}
