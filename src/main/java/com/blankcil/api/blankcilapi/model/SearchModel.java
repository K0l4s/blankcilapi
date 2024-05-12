package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchModel implements Serializable {
    private List<UserModel> users;
    private List<PodcastModel> podcasts;
}