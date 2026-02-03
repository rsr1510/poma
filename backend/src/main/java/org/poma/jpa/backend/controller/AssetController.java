package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService svc;

    public AssetController(AssetService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<List<Assets>> all() {
        List<Assets> assets = svc.findAll();
        if(assets.isEmpty()) {
            throw new IllegalArgumentException("No assets found");
        }
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assets> get(@PathVariable Long id) {
        if(id == null || id <= 0) throw new IllegalArgumentException("Invalid asset ID");
        if(svc.findById(id) == null) throw new IllegalArgumentException("Asset not found with ID: " + id);
        Assets a = svc.findById(id);
        return ResponseEntity.ok(a);
    }

    @PostMapping
    public ResponseEntity<Assets> create(@RequestBody Assets asset) {
        // basic validation
        if (asset == null) throw new IllegalArgumentException("Asset must not be null");
        if (asset.getName() == null || asset.getName().trim().isEmpty()) throw new IllegalArgumentException("Asset name is required");
        if (asset.getType() == null) throw new IllegalArgumentException("Asset type is required");

        Assets saved = svc.create(asset);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assets> update(@PathVariable Long id, @RequestBody Assets asset) {
        if (asset == null) throw new IllegalArgumentException("Asset must not be null");
        if (asset.getName() == null || asset.getName().trim().isEmpty()) throw new IllegalArgumentException("Asset name is required");
        Assets updated = svc.update(id, asset);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Note: GlobalExceptionHandler will handle ResourceNotFoundException and IllegalArgumentException
}
