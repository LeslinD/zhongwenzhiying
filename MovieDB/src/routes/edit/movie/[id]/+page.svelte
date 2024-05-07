<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte'
  import {goto} from '$app/navigation'
  import currentNavBar from '$lib/currentNavBar';
  import HeaderAdmin from '$lib/layouts/HeaderAdmin.svelte';
  
  $currentNavBar = HeaderAdmin;

  const id = $page.url.pathname.split('/')[3]

  let movie;
  let castList = [];
  let crewList = [];
  let error;
  let overview;

  async function load() {
    movie = await fetch(
			`https://api.themoviedb.org/3/movie/${id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
		).then(res => res.json());
    const actors = await fetch(
      `https://api.themoviedb.org/3/movie/${id}/credits?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US&page=1`
    ).then(resPerson => resPerson.json());
    castList = actors.cast || []
    crewList = actors.crew || []
  };

  const save = async () => {
    const res = await fetch('{API}/${id}', {
      method: 'POST',
      body: JSON.stringify({
        vote_average,
        overview
      }),
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (res.ok) {
      goto('/manage');
    } else {
      error = 'Save Failed. Please Check Your Network.'
  	}
  };

  const deleteMovie = async () => {
    const res = await fetch('{API}/${id}', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (res.ok) {
      goto('/manage');
    } else {
      error = 'Delete Failed. Please Check Your Network.';
  	}
  };

  onMount(() => load())

  $: genreNames = (movie?.genres || []).map(item => item.name).join(', ');
</script>

<!-- 标题设置 -->
<svelte:head>
	<title>{movie ? movie.title : 'Movie'} - MovieDB</title>
</svelte:head>

{#if movie}
<form on:submit|preventDefault={save}>
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
              <input bind:value={genreNames} type="text" />
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
      <textarea bind:value={movie.overview} class="w-full h-96 my-4"></textarea>
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
</form>

<div class="flex justify-center gap-4">
  <button
    type="submit"
    class="w-48 items-center rounded-lg bg-blue-500 hover:bg-blue-600 px-5 py-2.5 text-lg text-white ring-1 ring-inset ring-blue-700/10"
  >SUBMIT
  </button>
  <button on:click={deleteMovie}
    class="w-48 items-center rounded-lg bg-red-500 hover:bg-red-600 px-5 py-2.5 text-lg text-white ring-1 ring-inset ring-red-700/10"
  >DELETE ITEM
  </button>
</div>

{#if error}
  <div class="mt-2 text-red-600 text-sm transition-transform ease-out duration-300 transform">
      {error}
  </div>
{/if}

{:else}
<h2 class="m-auto text-2xl">Loading</h2>
{/if}
