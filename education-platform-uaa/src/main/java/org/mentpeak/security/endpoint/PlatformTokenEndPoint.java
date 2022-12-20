//package org.mentpeak.security.endpoint;
//
//import org.mentpeak.core.tool.api.Result;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * PlatformEndPoint
// *
// * @author mp
// */
//@Slf4j
//@RestController
//@AllArgsConstructor
//public class PlatformTokenEndPoint {
//
//    @GetMapping ( "/oauth/user-info" )
//    public Result < Authentication > currentUser ( Authentication authentication ) {
//        return Result.data ( authentication );
//    }
//
//}
