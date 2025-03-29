package org.example.swift.bank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @Operation(summary = "Get the bank by its swift code")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the bank",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BankDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank with the given swift code not found",
                    content = @Content
            )
    })
    @GetMapping("/{swift-code}")
    public ResponseEntity<BankDTO> getBank(@PathVariable("swift-code") String swiftCode) {
        BankDTO bankDTO = bankService.getBank(swiftCode);

        return ResponseEntity.status(HttpStatus.OK).body(bankDTO);
    }

    @Operation(summary = "Create the bank")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the bank",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data sent",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Bank with the given swift code already exists",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<MessageDTO> createBank(@Valid @RequestBody CreateBankDTO createBankDTO) {
        MessageDTO messageDTO = bankService.createBank(createBankDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }

    @Operation(summary = "Delete the bank by its swift code")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted the bank",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank with the given swift code not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{swift-code}")
    public ResponseEntity<MessageDTO> deleteBank(@PathVariable("swift-code") String swiftCode) {
        MessageDTO messageDTO = bankService.deleteBank(swiftCode);

        return ResponseEntity.status(HttpStatus.OK).body(messageDTO);
    }
}
