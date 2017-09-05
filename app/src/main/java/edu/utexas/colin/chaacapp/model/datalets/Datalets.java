package edu.utexas.colin.chaacapp.model.datalets;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datalets {

	private Map<String, Datalet> mDatalets = new HashMap<>();

	public List<Datalet> getDataletList() {
		return new ArrayList<>(mDatalets.values());
	}

	public List<Datalet> getDataletsOwnedBy(String userID) {
		List<Datalet> datalets = new ArrayList<>();

		for (Datalet d : getDataletList()) {
			if (userID.equals(d.getOwnerID())) {
				datalets.add(d);
			}
		}

		return datalets;
	}

	public void add(List<Datalet> datalets) {
		for (Datalet d : datalets) {
			if (d.getId() != null) {
				mDatalets.put(d.getId(), d);
			}
		}
	}

	public void add(Datalet datalet) {
		add(Collections.singletonList(datalet));
	}

	public Datalet get(String dataletID) {
		return mDatalets.get(dataletID);
	}

	public void removeDatalet(String dataletID) {
		mDatalets.remove(dataletID);
	}
}
