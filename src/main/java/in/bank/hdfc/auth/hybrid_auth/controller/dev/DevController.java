//package in.bank.hdfc.auth.hybrid_auth.controller.dev;
//
//import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
//import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/dev")
//public class DevController {
//
//    private final JwtUtil jwtUtil;
//
//    public DevController(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("/app-token")
//    public String generateAppToken(
//            @RequestParam String deviceId,
//            @RequestParam ClientType clientType
//    ) {
//        return jwtUtil.generateAppToken(deviceId, clientType);
//    }
//}