package com.universidad.apiprestamos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;
import com.universidad.apiprestamos.model.Rol;
import com.universidad.apiprestamos.model.Usuario;
import com.universidad.apiprestamos.repository.EquipoRepository;
import com.universidad.apiprestamos.repository.PrestamoRepository;
import com.universidad.apiprestamos.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApiPrestamosApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private EquipoRepository equipos;

    @Autowired
    private PrestamoRepository prestamos;

    @Autowired
    private PasswordEncoder encoder;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void preparar() throws Exception {
        prestamos.deleteAll();
        equipos.deleteAll();
        usuarios.deleteAll();
        usuarios.save(new Usuario("admin", encoder.encode("admin123"), Rol.ADMIN));
        usuarios.save(new Usuario("instructor", encoder.encode("user123"), Rol.USER));
        tokenAdmin = login("admin", "admin123");
        tokenUser = login("instructor", "user123");
    }

    // ---------- Seguridad ----------

    @Test
    void sinTokenResponde401() throws Exception {
        mvc.perform(get("/api/equipos")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoResponde401() throws Exception {
        mvc.perform(get("/api/equipos").header("Authorization", "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginIncorrectoResponde401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userPuedeConsultarPeroNoCrear() throws Exception {
        mvc.perform(get("/api/equipos").header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk());
        crearEquipo(tokenUser, "LAP-001").andExpect(status().isForbidden());
    }

    // ---------- CRUD y reglas de negocio ----------

    @Test
    void adminCreaConsultaActualizaEquipo() throws Exception {
        long id = idDe(crearEquipo(tokenAdmin, "LAP-001")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE")));

        mvc.perform(get("/api/equipos/" + id).header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPatrimonial").value("LAP-001"));

        mvc.perform(put("/api/equipos/" + id).header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigoPatrimonial\":\"LAP-001\",\"nombre\":\"Dell Latitude 5450\",\"tipo\":\"PORTATIL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dell Latitude 5450"));
    }

    @Test
    void validacionesYRecursosInexistentes() throws Exception {
        mvc.perform(post("/api/equipos").header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest());
        crearEquipo(tokenAdmin, "LAP-001").andExpect(status().isCreated());
        crearEquipo(tokenAdmin, "LAP-001").andExpect(status().isConflict());
        mvc.perform(get("/api/equipos/9999").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void cicloCompletoDePrestamo() throws Exception {
        long equipoId = idDe(crearEquipo(tokenAdmin, "PRY-001"));

        // Fecha prevista anterior a la de préstamo -> 400
        prestar(equipoId, "2026-10-28", "2026-10-20").andExpect(status().isBadRequest());

        long prestamoId = idDe(prestar(equipoId, "2026-10-28", "2026-10-30")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.equipo.estado").value("PRESTADO")));

        // Prestar otra vez el mismo equipo -> 409
        prestar(equipoId, "2026-10-28", "2026-10-30").andExpect(status().isConflict());

        // Eliminar con préstamo activo -> 409
        mvc.perform(delete("/api/equipos/" + equipoId).header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isConflict());

        // USER no puede devolver -> 403
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isForbidden());

        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"))
                .andExpect(jsonPath("$.equipo.estado").value("DISPONIBLE"));

        // Devolver dos veces -> 409
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/prestamos").header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Sin préstamo activo ya se puede eliminar -> 204
        mvc.perform(delete("/api/equipos/" + equipoId).header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    // ---------- Utilidades ----------

    private String login(String usuario, String clave) throws Exception {
        String cuerpo = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    private ResultActions crearEquipo(String token, String codigo) throws Exception {
        return mvc.perform(post("/api/equipos").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigoPatrimonial\":\"" + codigo + "\",\"nombre\":\"Equipo de prueba\",\"tipo\":\"PORTATIL\"}"));
    }

    private ResultActions prestar(long equipoId, String fecha, String prevista) throws Exception {
        return mvc.perform(post("/api/prestamos").header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equipoId\":" + equipoId + ",\"nombreSolicitante\":\"Ana Pérez\",\"fechaPrestamo\":\""
                        + fecha + "\",\"fechaPrevistaDevolucion\":\"" + prevista + "\"}"));
    }

    private long idDe(ResultActions resultado) throws Exception {
        Number id = JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
