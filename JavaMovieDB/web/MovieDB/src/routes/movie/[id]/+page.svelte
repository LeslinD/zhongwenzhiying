<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte'
  import { API } from '$lib/api'
  import currentNavBar from '$lib/currentNavBar';
  import Header from '$lib/layouts/Header.svelte';
  
  $currentNavBar = Header;
  
  const id = $page.url.pathname.split('/')[2]

  let movie;
  let genres;
  let castList = [];
  let crewList = [];
  let castPic = [];
  let crewPic = [];
  let picture;

  async function load() {
    const data = await fetch(
			`${API}/movieDetail?movie_id=${id}`
		).then(res => res.json());
    const picMovie = await fetch(
			`https://api.themoviedb.org/3/movie/${id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
		).then(resMovie => resMovie.json());
    const picPerson = await fetch(
      `https://api.themoviedb.org/3/movie/${id}/credits?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US&page=1`
    ).then(resPerson => resPerson.json());
    
    picture = picMovie.backdrop_path
    movie = data.movie;
    genres = data.genres;
    castList = data.castDetailList || [];
    crewList = data.crewDetailList || [];
    castPic = picPerson.cast || [];
    crewPic = picPerson.crew || [];
    let castLength = Math.min(castList.length, castPic.length);
    let crewLength = Math.min(crewList.length, crewPic.length);
    for (let i = 0; i < castLength; i++) {
      castList[i].url = castPic[i].profile_path;
    }
    for (let i = 0; i < crewLength; i++) {
      crewList[i].url = crewPic[i].profile_path;
    }
  }

  onMount(() => load())
</script>

<!-- 标题设置 -->
<svelte:head>
	<title>{movie ? movie.title : 'Movie'} - MovieDB</title>
</svelte:head>

{#if movie}
<section class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4 my-6">
  <div class="lg:col-span-6 flex flex-col items-start">
    <h2 class="text-4xl text-black font-extrabold my-2">
      {movie.title}
    </h2>
    <h4 class="text-xl text-gray-700 font-normal italic">{movie.tagline}</h4>
  </div>
  
  <div class="lg:col-span-3">
    <img src={`https://image.tmdb.org/t/p/original${picture}`} 
    alt={'Backdrop' || 'NO PICTURE FOUND'} class="rounded-md"
    >
  </div>
  
  <div class="lg:col-span-3 ml-4">
    <div class="flex items-center my-4">
      <h5 class="text-3xl text-amber-500 font-extrabold">{movie.vote_average.toFixed(1)}<span class="text-black">/10</span></h5>
      <p class="text-black font-extrabold ml-4">VOTE COUNT: <span class="text-amber-500">{movie.vote_count}</span></p>
    </div>
    <h3 class="text-2xl text-black font-semibold">Overview</h3>
    <p class="my-4">{movie.overview}</p>
  </div>

  <div class="lg:col-span-2">
    <h3 class="text-2xl text-black mb-4 font-semibold">Infomation</h3>
    <table class="table-fixed">
      <tbody class="text-left">
        <tr class="border-b-8 border-transparent">
          <th class="w-32">
            Genres
          </th>
          <td class="">
            {#each genres as genre}
              <a href={`/genre/${genre.id}`} class="inline-flex items-center rounded-md bg-blue-50 px-2 py-1 text-sm font-medium text-blue-700 ring-1 ring-inset ring-blue-700/10 mr-2">
                {genre.name}
              </a>
            {/each}
          </td>
        </tr>

        {#if movie.original_language}
        <tr class="border-y-8 border-transparent">
          <th class="">
            Original Language
          </th>
          <td class="">
            {movie.original_language}
          </td>
        </tr>
        {/if}

        {#if movie.releaseDate}
        <tr class="border-y-8 border-transparent">
          <th class="">
            Release Date
          </th>
          <td class="">
            {movie.releaseDate}
          </td>
        </tr>
        {/if}
        
        {#if movie.runtime}
        <tr class="border-y-8 border-transparent">
          <th class="">
            Runtime
          </th>
          <td class="">
            {movie.runtime} minutes
          </td>
        </tr>
        {/if}

        {#if movie.budget}
        <tr class="border-y-8 border-transparent">
          <th class="">
            Budget
          </th>
          <td class="">
            ${movie.budget}
          </td>
        </tr>
        {/if}

        {#if movie.revenue}
        <tr class="border-y-8 border-transparent">
          <th class="">
            Revenue
          </th>
          <td class="">
            ${movie.revenue}
          </td>
        </tr>
        {/if}

        {#if movie.popularity}
        <tr class="border-t-8 border-transparent">
          <th class="">
            Popularity
          </th>
          <td class="">
            {movie.popularity.toFixed(4)}
          </td>
        </tr>
        {/if}
      </tbody>
    </table>
  </div>
  
  <div class="lg:col-span-2 ml-4">
    <h3 class="text-2xl text-black font-semibold">Cast</h3>
    <div class="my-4 overflow-y-auto max-h-96">
      {#each castList as actor}
        <a href={`/person/${actor.detail.id}`} class="">
          <div class="flex gap-4 items-center mb-4">
              <img 
                  src={`https://image.tmdb.org/t/p/original${actor.url}`} 
                  alt={actor.detail.name||'Not found'}
                  class="w-[56px] h-[56px] object-cover rounded-full"
              >
              <div class="flex flex-col gap-1">
                <h2 class="text-[18px] font-bold uppercase">{actor.cast.character_name}</h2>
                <h3 class="text-1 text-amber-500 font-normal">{actor.detail.name}</h3>  
              </div>
          </div>
        </a>
      {/each}
    </div>
  </div>

  <div class="lg:col-span-2 ml-4">
    <h3 class="text-2xl text-black font-semibold">Crew</h3>
    <div class="my-4 overflow-y-auto max-h-96">
      {#each crewList as staff}
        <a href={`/person/${staff.detail.id}`} class="">
          <div class="flex gap-4 items-center mb-4">
              <img 
                  src={`https://image.tmdb.org/t/p/original${staff.url}`} 
                  alt={staff.detail.name||'Not found'}
                  class="w-[56px] h-[56px] object-cover rounded-full"
              >
              <div class="flex flex-col gap-1">
                <h2 class="text-[18px] font-bold uppercase">{staff.crew.job}</h2>
                <h3 class="text-1 text-amber-500 font-normal">{staff.detail.name}</h3>  
              </div>
          </div>
        </a>
      {/each}
    </div>
  </div>
</section>

{:else}
<h2 class="m-auto text-2xl">Loading...</h2>
{/if}
