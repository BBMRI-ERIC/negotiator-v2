# Negotiator-V2 (Legacy)

This version of the negotiator was deprecated superseded by a new implementation of the Negotiator, with a split into frontend and backend:

[Negotiator](https://github.com/BBMRI-ERIC/negotiator)

[Negotiator Frontend (BBMRI-ERIC frontend reference implementation)](https://github.com/BBMRI-ERIC/negotiator-frontend)

## Archived information

![build](https://github.com/BBMRI-ERIC/negotiator-v2/actions/workflows/build.yml/badge.svg?)
[![codecov](https://codecov.io/github/BBMRI-ERIC/negotiator-v2/branch/feature/github_actions/graph/badge.svg?token=8W0I985ZXI)](https://codecov.io/github/BBMRI-ERIC/negotiator-v2)
![Latest Release](https://img.shields.io/github/v/release/bbmri-eric/negotiator-v2)
![Docker Image Size (latest by date)](https://img.shields.io/docker/image-size/bbmrieric/negotiator)

## Purpose

The purpose of the negotiator is to serve academic researchers seeking bio-specimen and data for their research
by providing a place for structured negotiations with partner [biobanks](https://www.sciencedirect.com/topics/nursing-and-health-professions/biobank). By streamlining the entire negotiation process the BBMRI-ERIC negotiator
facilitates access and simplifies the communication between researchers and BBMRI-ERIC biobanks about availability of samples and data for research.

## Current state

Using the BBMRI-ERIC [Directory](https://directory.bbmri-eric.eu/#/) or the [GBA SampleLocator](https://samplelocator.bbmri.de/) researchers can browse and locate
a biobank's available resources, and then request access via the [Negotiator](https://negotiator.bbmri-eric.eu/).

## Getting Started
The simplest way to spin up a negotiator instance is using docker. The commands bellow will start an instance with test
data and the authentication disabled:

```sh
docker network create negotiator
docker run --name negotiator-db --network negotiator -p 5432:5432 -e POSTGRES_PASSWORD=negotiator -e POSTGRES_USER=negotiator -e POSTGRES_DB=negotiator -d postgres:14
docker run -d --name negotiator --network negotiator -p 8080:8080 -e POSTGRES_HOST="negotiator-db" -e AUTH="true"  bbmrieric/negotiator
```

## Documentation
* [Development](development.md)
* [Deployment](deployment.md)


