<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte'
  import { API, KEY } from '../../../lib/api'

  const id = $page.url.pathname.split('/')[2]

  let movie;
  let castList = [];
  let crewList = [];

  async function load() {
    movie = await fetch(
			`https://api.themoviedb.org/3/movie/${id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
		).then(res => res.json());
    const actors = await fetch(
      `https://api.themoviedb.org/3/movie/${id}/credits?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US&page=1`
    ).then(resPerson => resPerson.json());
    castList = actors.cast || []
    crewList = actors.crew || []
  }
  onMount(() => load())
</script>

<!-- 标题设置 -->
<svelte:head>
	<title>{movie ? movie.title : 'Movie'} - MovieDB</title>
</svelte:head>

{#if movie}
<section class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4 my-6">
  <div class="lg:col-span-6 flex justify-between items-center">
    <h2 class="text-4xl text-black font-extrabold my-2">
      {movie.title}
    </h2>
    <p class="text-3xl text-amber-500 font-extrabold">{movie.vote_average.toFixed(1)}<span class="text-black">/10</span></p>
  </div>
  
  <div class="lg:col-span-3">
    <img src={`https://image.tmdb.org/t/p/original${movie.backdrop_path}`} alt="Poster" class="rounded-md">
  </div>
  
  <div class="lg:col-span-3 ml-4">
    <h3 class="text-2xl text-black mb-4 font-semibold">Infomation</h3>
    <table class="table-fixed">
      <tbody class="text-left">
        <tr class="border-b-8 border-transparent">
          <th class="w-32">
            Genres
          </th>
          <td class="">
            {#each movie.genres as genre}
              <a href={`/genre/${genre.id}`} class="inline-flex items-center rounded-md bg-blue-50 px-2 py-1 text-sm font-medium text-blue-700 ring-1 ring-inset ring-blue-700/10 mr-2">
                {genre.name}
              </a>
            {/each}
          </td>
        </tr>
        <tr class="border-b-8 border-transparent">
          <th class="w-32">
            Companies
          </th>
          <td class="">
          { movie.production_companies.map(item => item.name).join(', ') }
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Countries
          </th>
          <td class="">
          { movie.production_countries.map(item => item.name).join(', ') }
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Spoken Languages
          </th>
          <td class="">
          { movie.spoken_languages?.map(item => item.name).join(', ') }
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Genres
          </th>
          <td class="">
          { movie.genres.map(item => item.name).join(', ') }
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Release Date
          </th>
          <td class="">
            {movie.release_date}
          </td>
        </tr>
        
        <tr class="border-y-8 border-transparent">
          <th class="">
            Runtime
          </th>
          <td class="">
            {movie.runtime} minutes
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Budget
          </th>
          <td class="">
            R${movie.budget.toFixed(2)}
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Revenue
          </th>
          <td class="">
            R${movie.revenue.toFixed(2)}
          </td>
        </tr>

        <tr class="border-t-8 border-transparent">
          <th class="">
            Vote Count
          </th>
          <td class="">
            {movie.vote_count}
          </td>
        </tr>

      </tbody>
    </table>
  </div>

  <div class="lg:col-span-2">
    <h3 class="text-2xl text-black font-semibold">Overview</h3>
    <p class="my-4">{movie.overview}</p>
  </div>
  
  <div class="lg:col-span-2 ml-4">
    <h3 class="text-2xl text-black font-semibold">Cast</h3>
    <div class="my-4 overflow-y-auto max-h-96">
      {#each castList as actor}
        <a href={`/person/${actor.id}`} class="">
          <div class="flex gap-4 items-center mb-4">
              <img 
                  src={`https://image.tmdb.org/t/p/original${actor.profile_path}`} 
                  alt={actor.name}
                  class="w-[56px] h-[56px] object-cover rounded-full"
              >
              <div class="flex flex-col gap-1">
                <h2 class="text-[18px] font-bold uppercase">{actor.character}</h2>
                <h3 class="text-1 text-amber-500 font-normal">{actor.name}</h3>  
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
        <a href={`/person/${staff.id}`} class="">
          <div class="flex gap-4 items-center mb-4">
              <img 
                  src={`https://image.tmdb.org/t/p/original${staff.profile_path}`} 
                  alt={staff.name}
                  class="w-[56px] h-[56px] object-cover rounded-full"
              >
              <div class="flex flex-col gap-1">
                <h2 class="text-[18px] font-bold uppercase">{staff.job}</h2>
                <h3 class="text-1 text-amber-500 font-normal">{staff.name}</h3>  
              </div>
          </div>
        </a>
      {/each}
    </div>
  </div>
</section>

{:else}
<h2 class="m-auto text-2xl">Loading</h2>
{/if}
