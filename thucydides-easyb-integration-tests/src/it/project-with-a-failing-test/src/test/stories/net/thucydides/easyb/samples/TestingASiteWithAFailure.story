package net.thucydides.easyb.samples

import net.thucydides.easyb.samples.pages.DemoSiteSteps

using "thucydides"

thucydides.uses_default_base_url "classpath:demosite/index.html"
thucydides.uses_steps_from DemoSiteSteps
thucydides.uses_driver "htmlunit"

//thucydides.testsUserStory UserStories.Whatever, in(Features.Whatever)

tags "someTag"

/**
 * Thucydides can manage pages for us.
 */

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
         demo_site.enter_values('Label 3', true)
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value('3')
    }
}


scenario "Select entry in dropdown list using nested steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.call_nested_steps("Label 1", true)
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '1'
    }
}

scenario "Select another entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.enter_values('Label 3', true)
    }
    and "another step is pending"
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '3'
    }
    and "this should be executed", {
        demo_site.should_have_selected_value '3'
    }
    and "so should this one", {
        demo_site.should_have_selected_value '3'
    }
    and "this one", {
        demo_site.should_have_selected_value '3'
    }
}


scenario "Unsuccessfully select another entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.enter_values('Label 3', true)
    }
    and "another step is pending"
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '2'
    }
    and "this should be skipped", {
        demo_site.should_have_selected_value '3'
    }
    and "so should this one", {
        demo_site.should_have_selected_value '3'
    }
    and "this one", {
        demo_site.should_have_selected_value '3'
    }
}


scenario "Triggering a failure", {
    given "we are on the Thucydides demo site again", {
    }
    when "the ussr triggers an assertion error", {
        demo_site.trigger_exception()
    }
    then "the broswer should still be closed", {
    }
}
