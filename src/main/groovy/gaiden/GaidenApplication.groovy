package gaiden

import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

@CompileStatic
class GaidenApplication {

    ApplicationContext applicationContext

    GaidenApplication() {
        def applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.scan(getClass().package.name)
        applicationContext.refresh()
        this.applicationContext = applicationContext
    }
}
