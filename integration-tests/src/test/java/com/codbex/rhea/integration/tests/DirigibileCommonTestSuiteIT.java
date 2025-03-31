package com.codbex.rhea.integration.tests;

import org.eclipse.dirigible.integration.tests.api.SecurityIT;
import org.eclipse.dirigible.integration.tests.api.java.messaging.MessagingFacadeIT;
import org.eclipse.dirigible.integration.tests.ui.tests.GitPerspectiveIT;
import org.eclipse.dirigible.integration.tests.ui.tests.HomepageRedirectIT;
import org.eclipse.dirigible.integration.tests.ui.tests.MailIT;
import org.eclipse.dirigible.integration.tests.ui.tests.TerminalIT;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({//
        GitPerspectiveIT.class, //
        HomepageRedirectIT.class, //
        MailIT.class, //
        MessagingFacadeIT.class, //
        SecurityIT.class, //
        TerminalIT.class//
})
public class DirigibileCommonTestSuiteIT {
}
