package com.orange.oss.prometheus.iaasexporter;

import com.orange.oss.prometheus.iaasexporter.model.Publiable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@Slf4j
public class UtilityTest {

    @Test
    public void purgeOldData() {
        Set<Publiable> old = new HashSet<>();
        Publiable p1 = Mockito.mock(Publiable.class);
        Publiable p2 = Mockito.mock(Publiable.class);

        old.add(p1);
        old.add(p2);
        Set<Publiable> newSet = new HashSet<>();
        newSet.add(p2);

        old.forEach(p -> log.info("old : {}", p));
        newSet.forEach(p -> log.info("new : {}", p));

        Utility.purgeOldData(old, newSet);
        old.forEach(p -> log.info("old : {}", p));
        newSet.forEach(p -> log.info("new : {}", p));
        verify(p1, times(1)).unpublishMetrics();
        verify(p2, times(0)).unpublishMetrics();
        verifyNoMoreInteractions(p1, p2);
        Utility.purgeOldData(old, newSet);
        verifyNoMoreInteractions(p1, p2);
    }
}