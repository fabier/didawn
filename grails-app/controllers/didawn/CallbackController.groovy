package didawn

import com.wrapper.spotify.models.User
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class CallbackController {

    SpService spService

    def index() {
        String accessToken = spService.handleCallbackRequest(request)
        session.setAttribute("accessToken", accessToken)
        User me = spService.getMe(accessToken)
        session.setAttribute("me", me)
        redirect controller: "public"
    }
}
