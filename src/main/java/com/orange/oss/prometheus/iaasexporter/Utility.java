package com.orange.oss.prometheus.iaasexporter;

import com.orange.oss.prometheus.iaasexporter.model.Publiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utility {


    public static void purgeOldData(Set<Publiable> oldSetPubliable, final Set<Publiable> setPubliable) {
        List<Publiable> purgeList = new ArrayList<>();
        for (Publiable publiable : oldSetPubliable) {
            if (!setPubliable.contains(publiable)) {
                purgeList.add(publiable);
            }
        }
        while (!purgeList.isEmpty()) {
            Publiable vm = purgeList.remove(0);
            vm.unpublishMetrics();
        }
        oldSetPubliable.clear();
        oldSetPubliable.addAll(setPubliable);
    }
}
