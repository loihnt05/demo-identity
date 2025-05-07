package com.superkids.demo_identity.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.superkids.demo_identity.dto.request.AuthenticationRequest;
import com.superkids.demo_identity.dto.request.IntrospectRequest;
import com.superkids.demo_identity.dto.request.LogoutRequest;
import com.superkids.demo_identity.dto.request.RefreshRequest;
import com.superkids.demo_identity.dto.response.AuthenticationResponse;
import com.superkids.demo_identity.dto.response.IntrospectResponse;
import com.superkids.demo_identity.entity.InvalidatedToken;
import com.superkids.demo_identity.entity.User;
import com.superkids.demo_identity.exception.AppException;
import com.superkids.demo_identity.exception.ErrorCode;
import com.superkids.demo_identity.repository.InvalidatedTokenRepository;
import com.superkids.demo_identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean invalid = true;
        try{
            verifyToken(token);
        }catch (AppException e){
            invalid = false;
        }
        return IntrospectResponse.builder()
                .valid(invalid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedToken = verifyToken(request.getToken());

        var jti = signedToken.getJWTClaimsSet().getJWTID();
        Date exp = signedToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(exp)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedToken.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedToken = verifyToken(request.getToken());

        String jti = signedToken.getJWTClaimsSet().getJWTID();
        Date exp = signedToken.getJWTClaimsSet().getExpirationTime();
        if(jti == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED);


        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(exp)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("superkids.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can not create token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role ->
            {
                joiner.add(role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions()
                            .forEach(permission -> joiner.add(permission.getName()));
                }
            });
        }
        return joiner.toString();
    }

}
