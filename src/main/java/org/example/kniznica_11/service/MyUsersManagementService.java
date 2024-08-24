package org.example.kniznica_11.service;

import org.example.kniznica_11.dto.ReqRes;
import org.example.kniznica_11.entity.Users;
import org.example.kniznica_11.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MyUsersManagementService {

    private final UsersRepository usersRepository;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public MyUsersManagementService(UsersRepository usersRepository, JWTUtils jwtUtils, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }



    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            Users users = new Users();
            users.setLoginName(registrationRequest.getUsername());
            users.setPassWord(passwordEncoder.encode(registrationRequest.getPassword()));
            Users savedUsers = usersRepository.save(users);

            if (savedUsers.getId() != null) {
                resp.setUsers(savedUsers);
                resp.setMessage("uzivatel vatvoreny");
                resp.setStatusCode(200);

                // Nastavenie predvolenej role, ak nie je špecifikovaná
                if (registrationRequest.getRole() == null || registrationRequest.getRole().isBlank()) {
                    users.setRole("USER");
                } else {
                    users.setRole(registrationRequest.getRole());
                }
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public ReqRes login(ReqRes loginRequest) {
        ReqRes resp = new ReqRes();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            var user = usersRepository.findByLoginName(loginRequest.getUsername()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(),user);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRole(user.getRole().toUpperCase());
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hrs");
            resp.setMessage("Úspešne ste sa prihlásili");

        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRegiest) {
        ReqRes response = new ReqRes();
        try {
            String ourName =jwtUtils.extractUsername(refreshTokenRegiest.getToken());
            Users users = usersRepository.findByLoginName(ourName).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRegiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRegiest.getToken());
                response.setExpirationTime("24Hrs");
                response.setMessage("Token refreshunty");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getAllUsers(){
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = usersRepository.findAll();
            if (!result.isEmpty()) {
                reqRes.setUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("úspešné");
            }else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("nenasiel sa ziadny uzivatel");
            }
            return reqRes;
        }catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("nastala chyba" + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUserById(Long id){
        ReqRes reqRes = new ReqRes();

        try {
            Users userById = usersRepository.findById(id).orElseThrow(()-> new RuntimeException("uzivatel nenajdeny"));
            reqRes.setUsers(userById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("uzivatel s id :" + id + " najdeny");
        }catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage(" chyba: " + e.getMessage());
        }
        return reqRes;
    }
     public ReqRes deleteUser(Long id){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userById = usersRepository.findById(id);
            if (userById.isPresent()) {
                usersRepository.deleteById(id);
                reqRes.setStatusCode(200);
                reqRes.setMessage("uzivatel vymazany");
            }else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("nenajdeny uzivatel");
            }
        }catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("chyba : " + e.getMessage());
        }
        return reqRes;
     }

     public ReqRes updateUser(Long id , Users updateUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userById = usersRepository.findById(id);
            if (userById.isPresent()) {
                Users usersExist = userById.get();
                usersExist.setRole(updateUser.getRole());

                if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
                    usersExist.setPassWord(passwordEncoder.encode(updateUser.getPassword()));
                }
                Users saveUser = usersRepository.save(usersExist);
                reqRes.setStatusCode(200);
                reqRes.setMessage("uzivatel aktualizovany");
                reqRes.setUsers(saveUser);
            }else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("nenajdeny uzivatel");
            }
        }catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("chyba pri aktualizacii uzivatela : " + e.getMessage());
        }
        return reqRes;
     }

     public ReqRes getMyInfo(String username) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<Users> usersOptional = usersRepository.findByLoginName(username);
            if (usersOptional.isPresent()) {
                reqRes.setUsers(usersOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("info vystavene");
            }
        }catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("chyba pri vypise info : " + e.getMessage());
        }
        return reqRes;
     }

    /*public ReqRes getMyInfo(String username) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<MyUsers> usersOptional = myUsersRepository.findByUsername(username);
            if (usersOptional.isPresent()) {
                reqRes.setMyUsers(usersOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("User information retrieved successfully.");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found.");
            }
        } catch (Exception e) {
            // Optional: Add logging here to capture the exception details
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error retrieving user information: " + e.getMessage());
        }

        return reqRes;
    }*/



}
