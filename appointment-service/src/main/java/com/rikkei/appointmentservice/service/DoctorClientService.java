package com.rikkei.appointmentservice.service;

import com.rikkei.appointmentservice.dto.ApiResponseError;
import com.rikkei.appointmentservice.dto.DoctorSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.rikkei.appointmentservice.dto.PatientSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DoctorClientService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String patientUrl;

    public DoctorClientService(RestTemplate restTemplate,
            @Value("${doctor-service.url:http://DOCTOR-SERVICE}") String baseUrl,
            @Value("${patient-service.url:http://PATIENT-SERVICE}") String patientUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.patientUrl = patientUrl;
    }

    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "getDoctorFallback")
    public Object getDoctor(Long id) {
        return restTemplate.getForObject(baseUrl + "/api/v1/doctors/{id}", DoctorSummary.class, id);
    }

    public Object getDoctorFallback(Long id, Throwable ex) {
        return ApiResponseError.of(503, "Hiện tại không thể kiểm tra thông tin bác sĩ, vui lòng thử lại sau vài giây");
    }

    public PatientSummary getPatient(Long id) {
        return restTemplate.getForObject(baseUrl.replace("DOCTOR-SERVICE", "PATIENT-SERVICE") + "/api/v1/patients/{id}", PatientSummary.class, id);
    }

}
