package org.example.swift.country;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/v1/swift-codes"))
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @Operation(summary = "Get all banks for the specific country by its iso2 code")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found the country",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CountryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", description = "Country with the given iso2 code not found",
                    content = @Content
            ),
    })
    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<CountryDTO> getCountry(@PathVariable("countryISO2code") String iso2Code) {
        CountryDTO countryDTO = countryService.getCountry(iso2Code);

        return ResponseEntity.status(HttpStatus.OK).body(countryDTO);
    }
}
