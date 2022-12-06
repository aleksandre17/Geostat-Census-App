package com.geostat.census_2024.data.model;

import java.util.ArrayList;
import java.util.List;

public class DistrictAreal {

    private String distr_code;
    private String distr_id;
    private String instr_id;
    private String munic_id;
    private String region_id;
    private String geometry;

    private List<HouseModel> houseModels;

    public String getDistr_code() {
        return distr_code;
    }

    public void setDistr_code(String distr_code) {
        this.distr_code = distr_code;
    }

    public String getDistr_id() {
        return distr_id;
    }

    public void setDistr_id(String distr_id) {
        this.distr_id = distr_id;
    }

    public String getInstr_id() {
        return instr_id;
    }

    public void setInstr_id(String instr_id) {
        this.instr_id = instr_id;
    }

    public String getMunic_id() {
        return munic_id;
    }

    public void setMunic_id(String munic_id) {
        this.munic_id = munic_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public List<HouseModel> getHouses() {
        return houseModels;
    }

    public void setHouses(List<HouseModel> houseModels) {
        if (this.houseModels == null) this.houseModels = new ArrayList<>();
        if (!houseModels.isEmpty()) {
            this.houseModels.addAll(houseModels);
        }
    }
}
