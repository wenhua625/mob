package push;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public abstract class UmengNotification {
	// This JSONObject is used for constructing the whole request string.
	protected final JSONObject rootJson = new JSONObject();
	
	
	// The app master secret
	protected String appMasterSecret;
	
	// Keys can be set in the root level
	protected static final HashSet<String> ROOT_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"appkey", "timestamp", "type", "device_tokens", "alias", "alias_type", "file_id", 
			"filter", "production_mode", "feedback", "description", "thirdparty_id"}));
	
	// Keys can be set in the policy level
	protected static final HashSet<String> POLICY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"start_time", "expire_time", "max_send_num"
	}));
	
	// Set predefined keys in the rootJson, for extra keys(Android) or customized keys(IOS) please 
	// refer to corresponding methods in the subclass.
	public abstract boolean setPredefinedKeyValue(String key, Object value) throws Exception;
	public void setAppMasterSecret(String secret) {
		appMasterSecret = secret;
	}
	
	public String getPostBody(){
		return rootJson.toString();
	}
	
	protected final String getAppMasterSecret(){
		return appMasterSecret;
	}
	
	protected void setProductionMode(Boolean prod) throws Exception {
    	setPredefinedKeyValue("production_mode", prod.toString());
    }

	///姝ｅ紡妯″紡
    public void setProductionMode() throws Exception {
    	setProductionMode(true);
    }

    ///娴嬭瘯妯″紡
    public void setTestMode() throws Exception {
    	setProductionMode(false);
    }

    ///鍙戦�娑堟伅鎻忚堪锛屽缓璁～鍐欍�
    public void setDescription(String description) throws Exception {
    	setPredefinedKeyValue("description", description);
    }
  	

}
