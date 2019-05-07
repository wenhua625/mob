package push;

import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONObject;

public abstract class AndroidNotification extends UmengNotification {
	// Keys can be set in the payload level
	protected static final HashSet<String> PAYLOAD_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"display_type"}));
	
	// Keys can be set in the body level
	protected static final HashSet<String> BODY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"ticker", "title", "text", "builder_id", "icon", "largeIcon", "img", "play_vibrate", "play_lights", "play_sound",
			"sound", "after_open", "url", "activity", "custom"}));

	public enum DisplayType{
		NOTIFICATION{public String getValue(){return "notification";}},///
		MESSAGE{public String getValue(){return "message";}};//
		public abstract String getValue();
	}
	public enum AfterOpenAction{
        go_app,//    
        go_url,//
        go_activity,//
        ctivity,
        go_custom//
	}
	// Set key/value in the rootJson, for the keys can be set please see ROOT_KEYS, PAYLOAD_KEYS, 
	// BODY_KEYS and POLICY_KEYS.
	@Override
	public boolean setPredefinedKeyValue(String key, Object value) throws Exception {
		if (ROOT_KEYS.contains(key)) {
			// This key should be in the root level
			rootJson.put(key, value);
		} else if (PAYLOAD_KEYS.contains(key)) {
			// This key should be in the payload level
			JSONObject payloadJson = null;
			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}
			payloadJson.put(key, value);
		} else if (BODY_KEYS.contains(key)) {
			// This key should be in the body level
			JSONObject bodyJson = null;
			JSONObject payloadJson = null;
			// 'body' is under 'payload', so build a payload if it doesn't exist
			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}
			// Get body JSONObject, generate one if not existed
			if (payloadJson.has("body")) {
				bodyJson = payloadJson.getJSONObject("body");
			} else {
				bodyJson = new JSONObject();
				payloadJson.put("body", bodyJson);
			}
			bodyJson.put(key, value);
		} else if (POLICY_KEYS.contains(key)) {
			// This key should be in the body level
			JSONObject policyJson = null;
			if (rootJson.has("policy")) {
				policyJson = rootJson.getJSONObject("policy");
			} else {
				policyJson = new JSONObject();
				rootJson.put("policy", policyJson);
			}
			policyJson.put(key, value);
		} else {
			if (key == "payload" || key == "body" || key == "policy" || key == "extra") {
				throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
			} else {
				throw new Exception("Unknown key: " + key);
			}
		}
		return true;
	}
	
	// Set extra key/value for Android notification
	public boolean setExtraField(String key, String value) throws Exception {
		JSONObject payloadJson = null;
		JSONObject extraJson = null;
		if (rootJson.has("payload")) {
			payloadJson = rootJson.getJSONObject("payload");
		} else {
			payloadJson = new JSONObject();
			rootJson.put("payload", payloadJson);
		}
		
		if (payloadJson.has("extra")) {
			extraJson = payloadJson.getJSONObject("extra");
		} else {
			extraJson = new JSONObject();
			payloadJson.put("extra", extraJson);
		}
		extraJson.put(key, value);
		return true;
	}
	
	//
	public void setDisplayType(DisplayType d) throws Exception {
		setPredefinedKeyValue("display_type", d.getValue());
	}
	///	
	public void setTicker(String ticker) throws Exception {
		setPredefinedKeyValue("ticker", ticker);
	}
	///
	public void setTitle(String title) throws Exception {
		setPredefinedKeyValue("title", title);
	}
	///
	public void setText(String text) throws Exception {
		setPredefinedKeyValue("text", text);
	}
	///
	public void setBuilderId(Integer builder_id) throws Exception {
		setPredefinedKeyValue("builder_id", builder_id);
	}
	///
	public void setIcon(String icon) throws Exception {
		setPredefinedKeyValue("icon", icon);
	}
	///
	public void setLargeIcon(String largeIcon) throws Exception {
		setPredefinedKeyValue("largeIcon", largeIcon);
	}
	///
	public void setImg(String img) throws Exception {
		setPredefinedKeyValue("img", img);
	}
	///
	public void setPlayVibrate(Boolean play_vibrate) throws Exception {
		setPredefinedKeyValue("play_vibrate", play_vibrate.toString());
	}
	//
	public void setPlayLights(Boolean play_lights) throws Exception {
		setPredefinedKeyValue("play_lights", play_lights.toString());
	}
	///
	public void setPlaySound(Boolean play_sound) throws Exception {
		setPredefinedKeyValue("play_sound", play_sound.toString());
	}
	///
	public void setSound(String sound) throws Exception {
		setPredefinedKeyValue("sound", sound);
	}
	///
	public void setPlaySound(String sound) throws Exception {
		setPlaySound(true);
		setSound(sound);
	}
	
	///
	public void goAppAfterOpen() throws Exception {
		setAfterOpenAction(AfterOpenAction.go_app);
	}
	public void goUrlAfterOpen(String url) throws Exception {
		setAfterOpenAction(AfterOpenAction.go_url);
		setUrl(url);
	}
	public void goActivityAfterOpen(String activity) throws Exception {
		setAfterOpenAction(AfterOpenAction.go_activity);
		setActivity(activity);
	}
	public void goCustomAfterOpen(String custom) throws Exception {
		setAfterOpenAction(AfterOpenAction.go_custom);
		setCustomField(custom);
	}
	public void goCustomAfterOpen(JSONObject custom) throws Exception {
		setAfterOpenAction(AfterOpenAction.go_custom);
		setCustomField(custom);
	}
	
	///
	public void setAfterOpenAction(AfterOpenAction action) throws Exception {
		setPredefinedKeyValue("after_open", action.toString());
	}
	public void setUrl(String url) throws Exception {
		setPredefinedKeyValue("url", url);
	}
	public void setActivity(String activity) throws Exception {
		setPredefinedKeyValue("activity", activity);
	}
	///can be a string of json
	public void setCustomField(String custom) throws Exception {
		setPredefinedKeyValue("custom", custom);
	}
	public void setCustomField(JSONObject custom) throws Exception {
		setPredefinedKeyValue("custom", custom);
	}
	
}
