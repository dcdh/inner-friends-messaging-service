package com.innerfriends.messaging.infrastructure;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;

import java.util.Comparator;

// https://www.wimdeblauwe.com/blog/2021/02/12/junit-5-test-class-orderer-for-spring-boot/

/**
 * Run test following this priority:
 * End by running E2ETest
 * Run Managed next
 * Run infra first
 *
 * E2E should be run first because it is complicated to maintain consistency using the kafka bus
 */
public class Junit5TestClassOrder implements ClassOrderer {

    @Override
    public void orderClasses(final ClassOrdererContext classOrdererContext) {
        classOrdererContext.getClassDescriptors().sort(Comparator.comparingInt(Junit5TestClassOrder::getOrder));
    }

    private static int getOrder(final ClassDescriptor classDescriptor) {
        if (classDescriptor.getTestClass().equals(E2ETest.class)) {
            return 1;
        } else if (classDescriptor.getTestClass().getPackageName().contains("usecase")) {
            return 2;
        } else {
            return 3;
        }
    }

}
