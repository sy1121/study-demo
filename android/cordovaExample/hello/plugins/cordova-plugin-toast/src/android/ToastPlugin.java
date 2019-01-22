package cordova.plugin.toast;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
/**
 * This class echoes a string called from JavaScript.
 */
public class ToastPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showToast")) {
            String message = args.getString(0);
            this.showToast(message, callbackContext);
            return true;
        }
        return false;
    }
    private void showToast(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
			Toast.makeText(cordova.getActivity(),message,Toast.LENGTH_LONG).show();
        } else {
            callbackContext.error("Expected one non-empty string argument.");
			Toast.makeText(cordova.getActivity(),"未成功返回",Toast.LENGTH_LONG).show();
        }
    }
}