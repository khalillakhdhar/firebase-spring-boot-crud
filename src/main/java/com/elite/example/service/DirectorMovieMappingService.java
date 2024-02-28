package com.elite.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.elite.example.constant.Entity;
import com.elite.example.dto.DirectorDto;
import com.elite.example.dto.MovieDto;
import com.elite.example.entity.Director;
import com.elite.example.entity.DirectorMovieMapping;
import com.elite.example.entity.Movie;
import com.google.cloud.firestore.Firestore;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DirectorMovieMappingService {

	private final Firestore firestore;

	public List<MovieDto> retreiveMoviesByDirectorId(final String directorId)
			throws InterruptedException, ExecutionException {

		final var mappingList = firestore.collection(Entity.DIRECTOR_MOVIE_MAPPING.getName())
				.whereEqualTo("directorId", directorId).get().get().getDocuments();

		return mappingList.stream().map(mapping -> {
			final var directorMovieMapping = mapping.toObject(DirectorMovieMapping.class);

			try {
				final var movieDocument = firestore.collection(Entity.MOVIE.getName())
						.document(directorMovieMapping.getMovieId()).get().get();
				final var movie = movieDocument.toObject(Movie.class);
				return MovieDto.builder().name(movie.getName()).durationInMinutes(movie.getDurationInMinutes())
						.id(movieDocument.getId()).build();
			} catch (InterruptedException | ExecutionException e) {
				return null;
			}
		}).collect(Collectors.toList());
	}

	public List<DirectorDto> retreiveDirectorByMovieId(final String movieId)
			throws InterruptedException, ExecutionException {

		final var mappingList = firestore.collection(Entity.DIRECTOR_MOVIE_MAPPING.getName())
				.whereEqualTo("movieId", movieId).get().get().getDocuments();

		return mappingList.stream().map(mapping -> {
			final var directorMovieMapping = mapping.toObject(DirectorMovieMapping.class);

			try {
				final var directorDocument = firestore.collection(Entity.DIRECTOR.getName())
						.document(directorMovieMapping.getDirectorId()).get().get();
				final var director = directorDocument.toObject(Director.class);
				return DirectorDto.builder().firstName(director.getFirstName()).lastName(director.getLastName())
						.country(director.getCountry()).id(directorDocument.getId()).build();
			} catch (InterruptedException | ExecutionException e) {
				return null;
			}
		}).collect(Collectors.toList());
	}

	public ResponseEntity<?> createMapping(final DirectorMovieMapping directorMovieMapping) {
		final var response = new JSONObject();
		final var mappingId = RandomStringUtils.randomAlphanumeric(10).toUpperCase();

		firestore.collection(Entity.DIRECTOR_MOVIE_MAPPING.getName()).document(mappingId).set(directorMovieMapping);

		response.put("id", mappingId);
		response.put("timestamp", LocalDateTime.now());
		return ResponseEntity.ok(response.toString());
	}

	public ResponseEntity<?> delete(String mappingId) {
		final var response = new JSONObject();

		firestore.collection(Entity.DIRECTOR_MOVIE_MAPPING.getName()).document(mappingId).delete();

		response.put("message", "Mapping Id: " + mappingId + " Successfully Deleted");
		response.put("timestamp", LocalDateTime.now());
		return ResponseEntity.ok(response.toString());
	}

}