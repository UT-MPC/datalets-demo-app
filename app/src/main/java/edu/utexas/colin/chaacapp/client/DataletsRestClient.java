package edu.utexas.colin.chaacapp.client;


import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.users.User;

public class DataletsRestClient {

	private static final TypeReference DATALET_REF = new TypeReference<Datalet>(){};
	private static final TypeReference DATALET_LIST_REF = new TypeReference<List<Datalet>>(){};
	private static final TypeReference USER_REF = new TypeReference<User>(){};
	private static final TypeReference USER_LIST_REF = new TypeReference<List<User>>(){};
	private static final String DATALETS_PATH = "datalets";
	private static final String USERS_PATH = "users";

	private static final String TAG = "DataletsRestClient";
    private static final String BASE_URL = "http://datalets-colinm.rhcloud.com/v1/";

	private static AsyncHttpClient client = new AsyncHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        client.addHeader("Content-Type", "application/json");
    }

	public static void getAllUsers(ListCallback<User> callback) {
		getList(USERS_PATH, null, callback, USER_LIST_REF);
	}

	public static void getAllDatalets(ListCallback<Datalet> callback) {
		getList(DATALETS_PATH, null, callback, DATALET_LIST_REF);
	}

	public static void getOwnedDatalets(String userID, ListCallback<Datalet> callback) {
		getList(USERS_PATH + "/" + userID + "/datalets?available=false", null, callback, DATALET_LIST_REF);
	}

	public static void getAvailableDatalets(String userID, ListCallback<Datalet> callback) {
		getList(USERS_PATH + "/" + userID + "/datalets?available=true", null, callback, DATALET_LIST_REF);
	}

	public static void getUsersWithAccess(String dataletID, ListCallback<User> callback) {
		getList(DATALETS_PATH + "/" + dataletID + "/users", null, callback, USER_LIST_REF);
	}

	public static void createUser(User user, ObjectCallback<User> callback) {
		try {
			postObject(USERS_PATH, mapper.writeValueAsString(user), callback, USER_REF);
		} catch (JsonProcessingException ex) {
			callback.onFailure();
		}
	}

	public static void updateUser(User user, ObjectCallback<User> callback) {
		try {
			putObject(USERS_PATH + "/" + user.getId(), mapper.writeValueAsString(user), callback, USER_REF);
		} catch (JsonProcessingException ex) {
			callback.onFailure();
		}
	}

	public static void deleteUser(String userID, ObjectCallback<User> callback) {
		deleteObject(USERS_PATH + "/" + userID, null, callback, USER_REF);
	}

	public static void createDatalet(Datalet datalet, ObjectCallback<Datalet> callback) {
		try {
			postObject(DATALETS_PATH, mapper.writeValueAsString(datalet), callback, DATALET_REF);
		} catch (JsonProcessingException ex) {
			callback.onFailure();
		}
	}

	public static void updateDatalet(Datalet datalet, ObjectCallback<Datalet> callback) {
		try {
			putObject(DATALETS_PATH + "/" + datalet.getId(), mapper.writeValueAsString(datalet), callback, DATALET_REF);
		} catch (JsonProcessingException ex) {
			callback.onFailure();
		}
	}

	public static void deleteDatalet(String dataletID, ObjectCallback<Datalet> callback) {
		deleteObject(DATALETS_PATH + "/" + dataletID, null, callback, DATALET_REF);
	}

    private static <T> void getObject(String url, Map<String, String> paramsMap, final ObjectCallback<T> callback, TypeReference type) {
        RequestParams params = convertParams(paramsMap);
        client.get(getAbsoluteUrl(url), params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
					T object = mapper.readValue(responseBody, type);
                    if (object != null) {
                        callback.onSuccess(object);
                    } else {
                        callback.onFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
				Log.e(TAG, "Status: " + statusCode + ", Response: " + responseBody);
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    private static <T> void getList(String url, Map<String, String> paramsMap, final ListCallback<T> callback, TypeReference type) {
        RequestParams params = convertParams(paramsMap);
        client.get(getAbsoluteUrl(url), params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
					Log.e(TAG, "responseBody: " + responseBody);
					List<T> list = mapper.readValue(responseBody, type);
                    if (list != null) {
                        callback.onSuccess(list);
                    } else {
                        callback.onFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
				Log.e(TAG, "Status: " + statusCode + ", Response: " + responseBody);
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    private static <T> void postObject(String url, String jsonBody, final ObjectCallback<T> callback, TypeReference type) {
        try {
            Log.e(TAG, "post: " + jsonBody);
            StringEntity entity = new StringEntity(jsonBody);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(null, getAbsoluteUrl(url), entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                    try {
						T object = mapper.readValue(responseBody, type);
                        if (object != null) {
                            callback.onSuccess(object);
                        } else {
                            callback.onFailure();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
					Log.e(TAG, "Failed with Status: " + statusCode + ", ResponseBody: " + responseBody);
                    error.printStackTrace();
                    callback.onFailure();
                }
            });
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            callback.onFailure();
        }
    }

    private static <T> void putObject(String url, String jsonBody, final ObjectCallback<T> callback, TypeReference type) {
		try {
			Log.e(TAG, "put: " + jsonBody);
			StringEntity entity = new StringEntity(jsonBody);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			client.put(null, getAbsoluteUrl(url), entity, "application/json", new TextHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseBody) {
					try {
						T object = mapper.readValue(responseBody, type);
						if (object != null) {
							callback.onSuccess(object);
						} else {
							callback.onFailure();
						}
					} catch (Exception e) {
						e.printStackTrace();
						callback.onFailure();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
					Log.e(TAG, "Status: " + statusCode + ", Response: " + responseBody);
					error.printStackTrace();
					callback.onFailure();
				}
			});
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			callback.onFailure();
		}
    }

    private static <T> void deleteObject(String url, Map<String, String> paramsMap, final ObjectCallback<T> callback, TypeReference type) {
        RequestParams params = convertParams(paramsMap);
        client.delete(getAbsoluteUrl(url), params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
					T object = mapper.readValue(responseBody, type);
                    callback.onSuccess(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
				Log.e(TAG, "Status: " + statusCode + ", Response: " + responseBody);
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static RequestParams convertParams(Map<String, String> paramsMap) {
        RequestParams params = new RequestParams();
        if (paramsMap != null) {
            for (String key : paramsMap.keySet()) {
                params.put(key, paramsMap.get(key));
            }
        }
        return params;
    }

    public interface ObjectCallback<T> {
        void onSuccess(T item);
        void onFailure();
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> list);
        void onFailure();
    }
}
