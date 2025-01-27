package didawn

import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class AlbumController {

    def show(String id) {
        render view: "index"
    }
}
