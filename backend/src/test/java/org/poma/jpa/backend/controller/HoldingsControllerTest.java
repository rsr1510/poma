package org.poma.jpa.backend.controller;

import org.junit.jupiter.api.Test;
import org.poma.jpa.backend.service.HoldingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HoldingsControllerTest {
    @Autowired
    public HoldingsController controller;

    @Test
    public void controllerIsInjected() {
        org.junit.jupiter.api.Assertions.assertNotNull(controller);
    }

    @Test
    public void controllerIsInstanceOfExpectedType() {
        org.junit.jupiter.api.Assertions.assertTrue(controller instanceof org.poma.jpa.backend.controller.HoldingsController);
    }
}
