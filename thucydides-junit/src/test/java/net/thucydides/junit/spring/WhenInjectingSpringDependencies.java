package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.spring.samples.dao.GizmoDao;
import net.thucydides.junit.spring.samples.service.BazingaService;
import net.thucydides.junit.spring.samples.service.GizmoService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(ThucydidesRunner.class)
@ContextConfiguration(locations = "/spring/config.xml")
public class WhenInjectingSpringDependencies {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    public GizmoService gizmoService;

    @Resource
    public GizmoDao gizmoDao;

    @Autowired
    @Qualifier("premiumBazingaService")
    public BazingaService premiumBazingaService;

    @Test
    public void shouldInstanciateGizmoService() {
        assertThat(gizmoService, is(not(nullValue())));
    }

    @Test
    public void shouldInstanciateNestedServices() {
        assertThat(gizmoService.getWidgetService(), is(not(nullValue())));
    }

    @Test
    public void shouldInstanciateServicesUsingTheResourceannotation() {
        assertThat(gizmoDao, is(not(nullValue())));
    }

    @Test
    public void shouldAllowQualifiers() {
        assertThat(premiumBazingaService.getName(), is("Premium Bazingas"));
    }

}